-- ============================================================
-- 团队协作系统 - 用户与权限模块（C 组）建表脚本
-- 库名：teamcollab_db   字符集：utf8mb4
-- 说明：先执行本脚本，再执行 data.sql 初始化角色、权限与账号
-- ============================================================

CREATE DATABASE IF NOT EXISTS teamcollab_db
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE teamcollab_db;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    account       VARCHAR(50)  NOT NULL COMMENT '登录账号（唯一）',
    nickname      VARCHAR(50)  NOT NULL COMMENT '昵称/显示名',
    email         VARCHAR(100)          DEFAULT NULL COMMENT '邮箱（唯一，用于找回）',
    phone         VARCHAR(20)           DEFAULT NULL COMMENT '手机号（展示时脱敏）',
    password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希（BCrypt，禁止存明文）',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 禁用',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除 1 已删除',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_account (account),
    UNIQUE KEY uk_user_email (email),
    KEY idx_user_status (status, deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ------------------------------------------------------------
-- 2. 角色表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_code   VARCHAR(50)  NOT NULL COMMENT '角色编码（唯一，如 SUPER_ADMIN）',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    description VARCHAR(200)          DEFAULT NULL COMMENT '角色说明',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 启用 0 禁用',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- ------------------------------------------------------------
-- 3. 权限表（权限点，粒度到“操作”）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    code      VARCHAR(80)  NOT NULL COMMENT '权限点编码，规范：“模块:动作”，如 task:assign',
    name      VARCHAR(50)  NOT NULL COMMENT '权限名称',
    type      VARCHAR(20)  NOT NULL DEFAULT 'ACTION' COMMENT '类型：MENU 菜单 / ACTION 操作',
    parent_id BIGINT       NOT NULL DEFAULT 0 COMMENT '父权限ID，0 表示顶级',
    sort      INT          NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (code),
    KEY idx_permission_parent (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '权限表';

-- ------------------------------------------------------------
-- 4. 用户-角色关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id    BIGINT   NOT NULL COMMENT '用户ID',
    role_id    BIGINT   NOT NULL COMMENT '角色ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_ur_role (role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户-角色关联表';

-- ------------------------------------------------------------
-- 5. 角色-权限关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id       BIGINT   NOT NULL COMMENT '角色ID',
    permission_id BIGINT   NOT NULL COMMENT '权限ID',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    KEY idx_rp_permission (permission_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色-权限关联表';
