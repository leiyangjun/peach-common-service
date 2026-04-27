-- 创作日期：2026-04-25，作者：leiyangjun — PostgreSQL 12+ 用户表 DDL（与 application 中 DataSource 一致）
-- 主键 ID 使用 BIGINT 存储 64 位雪花，与 org.peach.common.utils.IdUtil#nextId() 及 long/Long 实体字段一致
-- 小写表名/列名，便于与 MyBatis mapUnderscoreToCamelCase 及常规实体映射
-- 执行前请在目标库中：CREATE DATABASE peach_common; 并 \c peach_common 后再执行本脚本

SET client_encoding = 'UTF8';
SET search_path = public;

-- 主体类型：系统侧账号（后台/员工）与 C 端账号分域；开放 API / OAuth 可通过 open_union_id 与业务扩展表关联
CREATE TABLE IF NOT EXISTS public.cmn_user (
    id                 BIGINT         NOT NULL,
    subject_type       VARCHAR(16)    NOT NULL DEFAULT 'INTERNAL',
    username           VARCHAR(64)             NULL,
    password           VARCHAR(128)            NULL,
    nickname           VARCHAR(64)             NULL,
    real_name          VARCHAR(64)             NULL,
    mobile             VARCHAR(20)             NULL,
    email              VARCHAR(128)            NULL,
    avatar_url         VARCHAR(512)            NULL,
    gender             SMALLINT                NULL DEFAULT 0,
    register_client    VARCHAR(32)             NULL,
    last_login_time    TIMESTAMPTZ(3)          NULL,
    last_login_client  VARCHAR(32)             NULL,
    open_union_id      VARCHAR(128)            NULL,
    remark             VARCHAR(500)            NULL,
    valid              CHAR(1)        NOT NULL DEFAULT '1',
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_user PRIMARY KEY (id),
    CONSTRAINT ck_cmn_user_valid CHECK (valid IN ('0', '1')),
    CONSTRAINT ck_cmn_user_subject_type CHECK (subject_type IN ('INTERNAL', 'CUSTOMER')),
    CONSTRAINT ck_cmn_user_gender CHECK (gender IS NULL OR gender IN (0, 1, 2)),
    CONSTRAINT ck_cmn_user_internal_login CHECK (
        subject_type <> 'INTERNAL'
        OR (username IS NOT NULL AND btrim(username) <> '')
    )
);

COMMENT ON TABLE public.cmn_user IS '用户：区分系统侧（INTERNAL）与 C 端（CUSTOMER）；终端与开放标识字段便于审计与对接 OAuth/开放 API';
COMMENT ON COLUMN public.cmn_user.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_user.subject_type IS '主体：INTERNAL=系统侧（运营/员工等），CUSTOMER=C 端消费者';
COMMENT ON COLUMN public.cmn_user.username IS '登录名；系统侧必填（由约束保证），C 端可空（手机/三方为主）';
COMMENT ON COLUMN public.cmn_user.password IS '密码摘要；免密或纯三方登录可为空';
COMMENT ON COLUMN public.cmn_user.nickname IS '昵称';
COMMENT ON COLUMN public.cmn_user.real_name IS '真实姓名或对内展示名';
COMMENT ON COLUMN public.cmn_user.mobile IS '手机号';
COMMENT ON COLUMN public.cmn_user.email IS '邮箱';
COMMENT ON COLUMN public.cmn_user.avatar_url IS '头像地址';
COMMENT ON COLUMN public.cmn_user.gender IS '性别：0 未知，1 男，2 女';
COMMENT ON COLUMN public.cmn_user.register_client IS '首次注册终端：如 ADMIN_WEB、MINI_APP、APP、OPEN_API（与网关/客户端约定枚举）';
COMMENT ON COLUMN public.cmn_user.last_login_time IS '最近登录时间';
COMMENT ON COLUMN public.cmn_user.last_login_client IS '最近登录终端，含义同 register_client';
COMMENT ON COLUMN public.cmn_user.open_union_id IS '开放体系全局标识预留（如对接方用户 id、OAuth subject 等）；细粒度授权建议另建关联表';
COMMENT ON COLUMN public.cmn_user.remark IS '备注';
COMMENT ON COLUMN public.cmn_user.valid IS '是否有效：1=有效 0=无效（逻辑删除）';
COMMENT ON COLUMN public.cmn_user.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_user.edit_time IS '最后更新时间';

-- 仅对非空登录名唯一（PostgreSQL 中 UNIQUE 允许多个 NULL，此处显式索引便于与 C 端可空 username 共存）
CREATE UNIQUE INDEX IF NOT EXISTS uk_cmn_user_username ON public.cmn_user (username) WHERE username IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_cmn_user_mobile ON public.cmn_user (mobile) WHERE mobile IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_cmn_user_email ON public.cmn_user (email) WHERE email IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_cmn_user_open_union ON public.cmn_user (open_union_id) WHERE open_union_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_cmn_user_subject_type ON public.cmn_user (subject_type);
CREATE INDEX IF NOT EXISTS idx_cmn_user_valid ON public.cmn_user (valid);

-- 演示账号：admin / 123456（BCrypt）；生产请删除或改密
INSERT INTO public.cmn_user (
    id, subject_type, username, password, nickname, register_client, valid
) VALUES (
    1970000000000000001,
    'INTERNAL',
    'admin',
    '$2a$10$GIIvgyd4HHBkallosEtuF.I7qnF76zaDUDqlaB41IWT1mDfDPPj6C',
    '系统管理员',
    'ADMIN_WEB',
    '1'
) ON CONFLICT (id) DO NOTHING;

-- 若此前已执行过带「edit_time 触发器」的旧版脚本，可手工清理（无则略过）：
-- DROP TRIGGER IF EXISTS trg_cmn_user_bu ON public.cmn_user;
-- DROP FUNCTION IF EXISTS public.trg_f_cmn_user_touch_edit_time();
