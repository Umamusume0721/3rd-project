# TeamCollab 后端 —— 用户与权限模块（C 组 / 陈浩天）

团队协作系统三阶段项目，C 组负责的「用户与权限」后端模块，可直接用 IDEA 打开运行。

- 技术栈：Spring Boot 3.2.5 + MyBatis-Plus 3.5.7 + MySQL 8 + JWT + BCrypt
- JDK：17
- 端口：8080
- 数据库：`teamcollab_db`

## 一、快速启动（4 步）

1. **建库建表**：用 Navicat / MySQL 命令行依次执行 `src/main/resources/db/schema.sql`、`db/data.sql`
   ```sql
   source E:/teamcollab/src/main/resources/db/schema.sql;
   source E:/teamcollab/src/main/resources/db/data.sql;
   ```
2. **改数据库账号**：打开 `src/main/resources/application.yml`，把 `username` / `password` 改成你本机 MySQL 的。
3. **启动**：IDEA 里打开 `E:\teamcollab`（Maven 工程），运行 `TeamCollabApplication`。
   命令行启动：
   ```cmd
   set JAVA_HOME=E:\jdk
   E:\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
   ```
4. **验证**：访问 `POST http://localhost:8080/api/auth/login`，或用下面的 curl。

> 首次用 IDEA 打开若实体类飘红：Settings → Build → Compiler → Annotation Processors 勾选 **Enable annotation processing**（实体用了 Lombok 的 @Data）。

## 二、初始账号（密码统一 123456）

| 账号 | 昵称 | 角色 |
|---|---|---|
| `admin` | 系统管理员 | SUPER_ADMIN（全部权限） |
| `chenhaotian` | 陈浩天 | TEAM_OWNER |
| `wanghaoyu` | 王浩宇 | TEAM_OWNER |
| `fangkan` | 方侃 | MEMBER |
| `guoyufeng` | 郭雨峰 | MEMBER |
| `dujunhao` | 杜君豪 | MEMBER |

登录示例：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"chenhaotian\",\"password\":\"123456\"}"
```

返回：

```json
{"code":0,"msg":"成功","data":{"token":"eyJhbGciOi...","tokenType":"Bearer","expiresIn":7200,
 "userId":2,"account":"chenhaotian","nickname":"陈浩天",
 "roles":["TEAM_OWNER"],"permissions":["task:create","task:update","..."]}}
```

之后每个请求带 `Authorization: Bearer {token}`。

## 三、接口清单（详见 docs/用户权限API接口文档.md）

| 方法 | 路径 | 权限点 | 说明 |
|---|---|---|---|
| POST | /api/auth/login | 无需 Token | 登录，返回 Token |
| POST | /api/auth/logout | 登录即可 | 登出，Token 立即失效 |
| GET | /api/auth/me | 登录即可 | 当前用户信息（含角色与权限点） |
| GET | /api/users | user:list | 用户分页（关键字/状态/角色筛选） |
| GET | /api/users/me | 登录即可 | 当前用户（同 auth/me） |
| PUT | /api/users/me | 登录即可（仅本人） | 修改自己的资料 |
| GET | /api/users/{id} | user:view | 用户详情 |
| PUT | /api/users/{id}/roles | user:assign-role | 覆盖式分配角色 |
| PUT | /api/users/{id}/status?status=0/1 | user:disable | 启用 / 禁用 |
| GET | /api/roles | role:list | 角色列表 |
| GET | /api/permissions | role:list | 全部权限点 |
| GET | /api/roles/{id}/permissions | role:list | 某角色的权限 |
| PUT | /api/roles/{id}/permissions | role:assign-permission | 配置角色权限 |

统一返回：`{ "code": 0, "msg": "成功", "data": ... }`；错误码见 `common/ErrorCode.java` 与 API 文档。

## 四、目录结构

```
com.teamcollab
├── TeamCollabApplication.java      启动类
├── common                          统一返回 R、错误码、业务异常、全局异常处理
├── config                          MyBatis-Plus 分页、BCrypt、拦截器与 CORS
├── security                        登录认证与权限校验的全部实现
│   ├── JwtUtil.java                JWT 签发 / 解析
│   ├── TokenBlacklist.java         登出失效名单（内存版，可换 Redis）
│   ├── AuthInterceptor.java        拦截器：解析 Token → 写上下文 → 判 @RequirePermission
│   ├── RequirePermission.java      权限注解
│   ├── UserContext.java            ThreadLocal 用户上下文
│   ├── LoginUser.java              当前用户模型（含权限判定方法）
│   └── DataScope.java              数据级权限判定入口（给业务模块复用）
└── user                            用户与权限业务
    ├── entity / mapper / service / controller / dto / vo / constant
resources
├── application.yml                 数据源、MyBatis-Plus、JWT 配置
├── db/schema.sql                   建表脚本（5 张表）
├── db/data.sql                     角色、权限点、账号初始化
└── mapper/*.xml                    手写 SQL（登录查账号、分页、三表关联权限）
```

## 五、几个必看的设计点

1. **密码**：只存 BCrypt 哈希，比对用 `passwordEncoder.matches()`；登录失败统一返回「账号或密码错误」，不区分账号不存在，防止枚举账号。
2. **Token**：JWT 无状态，登出必须靠 `TokenBlacklist`（内存 Map）。上线换 Redis：`SET blacklist:{jti} 1 EX 剩余秒数`，改动只在这一个类里。
3. **权限校验**：接口上写 `@RequirePermission(PermCodes.USER_LIST)`，拦截器取当前用户权限集合比对；`SUPER_ADMIN` 直接放行。权限集合按 userId 缓存 5 分钟，角色变更时 `evict(userId)`，角色授权变更时 `clearAll()`。
4. **上下文**：业务代码用 `UserContext.getUserId()` 取当前用户，**不要**信任前端传的 userId。
5. **数据级权限**：功能权限之外的「这条数据你能不能动」，用 `DataScope.requireTeamOwnerOrAdmin(ownerId)` / `requireSelfOrAdmin(userId)`，团队/项目/任务的归属字段由 D 组提供。
6. **手机号脱敏**：列表与详情返回 `138****0002`，原始值只在库里。
7. **逻辑删除**：`sys_user.deleted` 由 MyBatis-Plus `@TableLogic` 自动处理，手写 SQL 里显式写了 `deleted = 0`。

## 六、与队友的对接

| 对象 | 你要提供 | 说明 |
|---|---|---|
| 方侃 / 郭雨峰（前端） | 登录接口返回结构、`Authorization: Bearer {token}` 放置方式、错误码表 | 前端按 code 判断：40101 未登录跳登录页，40102 Token 过期也跳登录页（或静默刷新），40301 提示无权限 |
| 王浩宇（核心业务） | `UserContext.getUserId()` 取当前用户、`@RequirePermission` 权限点、`DataScope` 判定入口 | 他的业务接口直接复用，不用再解析 Token |
| 杜君豪（测试） | 6 个测试账号（含 admin 与三种角色）、错误码表 | 越权用例直接用 member 账号访问 admin 接口验证 40301 |

## 七、还没做的（按排期属于 D 组或后续）

- 团队 / 项目 / 任务的表与接口：王浩宇负责，本模块只预置了权限点。
- 注册接口：方案里没有要求，`WebMvcConfig` 已预留 `/api/auth/register` 放行路径。
- Token 自动续期（refresh token）：当前是 2 小时过期后重新登录，前端按 40102 处理即可。
