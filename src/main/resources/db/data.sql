-- ============================================================
-- 团队协作系统 - 用户与权限模块（C 组）初始化数据
-- 前置：已执行 schema.sql
-- 初始密码统一为 123456（BCrypt 哈希，禁止改成明文）
-- ============================================================

USE teamcollab_db;

-- ---------------- 角色 ----------------
INSERT INTO sys_role (id, role_code, role_name, description, status) VALUES
(1, 'SUPER_ADMIN', '超级管理员', '系统级管理员，拥有全部权限', 1),
(2, 'TEAM_OWNER',  '团队负责人', '创建团队、管理成员、管理项目与任务', 1),
(3, 'MEMBER',      '成员',       '参与项目与任务，可新建任务、评论', 1),
(4, 'GUEST',       '访客',       '只读访问团队内数据', 1);

-- ---------------- 权限点 ----------------
-- 用户与权限（C 组负责）
INSERT INTO sys_permission (id, code, name, type, parent_id, sort) VALUES
(1,  'user:list',          '查看用户列表', 'ACTION', 0, 1),
(2,  'user:view',          '查看用户详情', 'ACTION', 0, 2),
(3,  'user:assign-role',   '分配用户角色', 'ACTION', 0, 3),
(4,  'user:disable',       '禁用/启用用户', 'ACTION', 0, 4),
(5,  'role:list',          '查看角色列表', 'ACTION', 0, 5),
(6,  'role:update',        '维护角色',   'ACTION', 0, 6),
(7,  'role:assign-permission', '配置角色权限', 'ACTION', 0, 7);

-- 核心业务（D 组负责，此处仅预置权限点，接口由王浩宇实现）
INSERT INTO sys_permission (id, code, name, type, parent_id, sort) VALUES
(11, 'team:create',        '创建团队',   'ACTION', 0, 11),
(12, 'team:update',        '编辑团队',   'ACTION', 0, 12),
(13, 'team:invite',        '邀请成员',   'ACTION', 0, 13),
(14, 'team:remove-member', '移除成员',   'ACTION', 0, 14),
(15, 'team:transfer',      '转让团队',   'ACTION', 0, 15),
(21, 'project:create',     '创建项目',   'ACTION', 0, 21),
(22, 'project:update',     '编辑项目',   'ACTION', 0, 22),
(23, 'project:delete',     '删除项目',   'ACTION', 0, 23),
(31, 'task:create',        '创建任务',   'ACTION', 0, 31),
(32, 'task:update',        '编辑任务',   'ACTION', 0, 32),
(33, 'task:assign',        '指派负责人', 'ACTION', 0, 33),
(34, 'task:delete',        '删除任务',   'ACTION', 0, 34),
(41, 'comment:create',     '发表评论',   'ACTION', 0, 41);

-- ---------------- 角色授权 ----------------
-- 超级管理员：全部权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 团队负责人：除用户与角色管理外的全部业务权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE code NOT LIKE 'user:%' AND code NOT LIKE 'role:%';

-- 成员：任务与评论
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 31), (3, 32), (3, 33), (3, 41);

-- 访客：只读，无写权限（此处不授予任何 ACTION 权限）
-- 若需要“只读”能力，应在业务接口内部按 role_code 判定，而不是授予写权限

-- ---------------- 账号 ----------------
-- 密码均为 123456 的 BCrypt 哈希
INSERT INTO sys_user (id, account, nickname, email, phone, password_hash, status, deleted) VALUES
(1, 'admin',       '系统管理员', 'admin@teamcollab.com',       '13800000001', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0),
(2, 'chenhaotian', '陈浩天',     'chenhaotian@teamcollab.com','13800000002', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0),
(3, 'wanghaoyu',   '王浩宇',     'wanghaoyu@teamcollab.com',  '13800000003', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0),
(4, 'fangkan',     '方侃',       'fangkan@teamcollab.com',    '13800000004', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0),
(5, 'guoyufeng',   '郭雨峰',     'guoyufeng@teamcollab.com',  '13800000005', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0),
(6, 'dujunhao',    '杜君豪',     'dujunhao@teamcollab.com',   '13800000006', '$2b$10$J3eBZVVVT8kOXHAx5RCd6.eDJEuJbxCm3rk.sPCE/zIHwD/X6HfV2', 1, 0);

-- 角色分配
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin 超级管理员
(2, 2),  -- 陈浩天 团队负责人
(3, 2),  -- 王浩宇 团队负责人
(4, 3),  -- 方侃 成员
(5, 3),  -- 郭雨峰 成员
(6, 3);  -- 杜君豪 成员
