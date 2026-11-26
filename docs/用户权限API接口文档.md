# 用户与权限 API 接口文档（C 组）

- 版本：v1.0
- 服务地址：`http://localhost:8080`
- 编码：UTF-8，`Content-Type: application/json`
- 认证：除登录外，所有接口需带请求头 `Authorization: Bearer {token}`

## 一、统一约定

### 1.1 返回结构

```json
{ "code": 0, "msg": "成功", "data": { } }
```

`code` 为 0 表示成功；非 0 时 `data` 为 null，`msg` 可直接展示给用户。

### 1.2 错误码

| code | 含义 | 前端建议处理 |
|---|---|---|
| 0 | 成功 | — |
| 40001 | 参数校验失败 | 提示 msg 内容（含字段名） |
| 40101 | 未登录或未携带 Token | 跳转登录页 |
| 40102 | Token 已失效（过期 / 已登出） | 跳转登录页或静默刷新 |
| 40103 | 账号或密码错误 | 提示，不区分账号不存在 |
| 40104 | 账号已被禁用 | 提示联系管理员 |
| 40301 | 无权限访问 | 提示「无权限」，按钮应提前隐藏 |
| 40401 | 资源不存在 | 提示或刷新列表 |
| 40902 | 操作冲突 | 提示冲突原因 |
| 50000 | 服务器内部错误 | 提示稍后再试 |

### 1.3 Token 使用

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9....
```

有效期 7200 秒（`teamcollab.jwt.expire-minutes` 可调）。

### 1.4 角色与权限点

| 角色编码 | 名称 | 权限范围 |
|---|---|---|
| SUPER_ADMIN | 超级管理员 | 全部权限点（代码层直接放行） |
| TEAM_OWNER | 团队负责人 | 除 `user:*`、`role:*` 外的业务权限 |
| MEMBER | 成员 | `task:create`、`task:update`、`task:assign`、`comment:create` |
| GUEST | 访客 | 无写权限，只读由业务接口按角色判定 |

权限点编码规范：`模块:动作`，如 `user:assign-role`、`task:assign`。完整清单见 `/api/permissions`。

## 二、接口明细

### 2.1 登录

`POST /api/auth/login`　权限：无需 Token

请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| account | string | 是 | 登录账号 |
| password | string | 是 | 密码（明文传输，生产必须上 HTTPS） |

```json
{ "account": "chenhaotian", "password": "123456" }
```

响应 data：

| 字段 | 类型 | 说明 |
|---|---|---|
| token | string | JWT |
| tokenType | string | 固定 `Bearer` |
| expiresIn | number | 有效期（秒） |
| userId | number | 用户 ID |
| account | string | 账号 |
| nickname | string | 昵称 |
| roles | array | 角色编码列表 |
| permissions | array | 权限点列表，前端据此控制按钮显隐 |

失败：`40103` 账号或密码错误；`40104` 账号已禁用；`40001` 参数为空。

### 2.2 登出

`POST /api/auth/logout`　权限：登录即可

请求头带当前 Token 即可，无需请求体。成功后该 Token 立即进入失效名单，再用返回 `40102`。

### 2.3 当前用户信息

`GET /api/auth/me`　权限：登录即可

响应 data：`id / account / nickname / email / phone(脱敏) / status / roles / permissions / createdAt`

### 2.4 用户分页列表

`GET /api/users`　权限：`user:list`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| keyword | string | 否 | 账号或昵称模糊匹配 |
| status | number | 否 | 1 启用 / 0 禁用，不传为全部 |
| roleCode | string | 否 | 按角色编码筛选，如 `TEAM_OWNER` |
| page | number | 否 | 默认 1 |
| size | number | 否 | 默认 10，上限 100 |

响应 data：

```json
{
  "records": [
    {"id":2,"account":"chenhaotian","nickname":"陈浩天","email":"chenhaotian@teamcollab.com",
     "phone":"138****0002","status":1,"roleCodes":"TEAM_OWNER","roleNames":"团队负责人",
     "createdAt":"2026-11-26 09:00:00"}
  ],
  "total": 6, "size": 10, "current": 1, "pages": 1
}
```

> `roleCodes`、`roleNames` 是逗号分隔字符串，多角色时如 `MEMBER,TEAM_OWNER`。

### 2.5 用户详情

`GET /api/users/{id}`　权限：`user:view`

响应 data 同 2.3。用户不存在返回 `40401`。

### 2.6 修改当前用户资料

`PUT /api/users/me`　权限：登录即可（仅本人，越权改别人返回 `40301`）

```json
{ "nickname": "陈浩天", "email": "chenhaotian@teamcollab.com", "phone": "13800000002" }
```

三个字段都可选；邮箱格式不正确 `40001`，邮箱重复 `40902`。

### 2.7 分配角色（覆盖式）

`PUT /api/users/{id}/roles`　权限：`user:assign-role`

```json
{ "roleIds": [2] }
```

传空数组 `40001`；存在无效角色 ID `40001`。成功后该用户权限缓存立即失效。

### 2.8 启用 / 禁用用户

`PUT /api/users/{id}/status?status=0`　权限：`user:disable`

`status` 只接受 0 或 1。禁用只拦登录，不删数据。

### 2.9 角色列表

`GET /api/roles`　权限：`role:list`

响应 data：`[{id, roleCode, roleName, description, status}]`

### 2.10 全部权限点

`GET /api/permissions`　权限：`role:list`

响应 data：`[{id, code, name, type, parentId, sort}]`

### 2.11 某角色的权限

`GET /api/roles/{id}/permissions`　权限：`role:list`

### 2.12 配置角色权限（覆盖式）

`PUT /api/roles/{id}/permissions`　权限：`role:assign-permission`

```json
{ "permissionIds": [1,2,3,5,31,32] }
```

`permissionIds` 可为空数组（清空该角色权限）。`SUPER_ADMIN` 不可裁剪，返回 `40902`。成功后全局权限缓存清空。

## 三、调用示例

```bash
# 1. 登录拿 token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"admin\",\"password\":\"123456\"}"

# 2. 用 token 查用户列表
curl "http://localhost:8080/api/users?page=1&size=10&roleCode=TEAM_OWNER" \
  -H "Authorization: Bearer {token}"

# 3. 越权验证（用 member 账号的 token 调管理员接口），应返回 40301
curl -X PUT "http://localhost:8080/api/users/2/roles" \
  -H "Authorization: Bearer {member_token}" -H "Content-Type: application/json" \
  -d "{\"roleIds\":[1]}"

# 4. 登出后再调任何接口，应返回 40102
curl -X POST http://localhost:8080/api/auth/logout -H "Authorization: Bearer {token}"
```

## 四、给前端的两条约定

1. 登录成功后把 `token` 存本地（网页 localStorage / 小程序 storage），每次请求统一在拦截器里拼 `Authorization`。
2. 登录返回的 `permissions` 用来控制按钮显隐，但**按钮隐藏不等于安全**，后端每个接口都会再判一次，前端不要依赖 UI 隐藏做权限控制。
