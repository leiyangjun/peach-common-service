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
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_user PRIMARY KEY (id),
    CONSTRAINT ck_cmn_user_valid CHECK (valid IN (0, 1)),
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
COMMENT ON COLUMN public.cmn_user.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_user.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_user.editor IS '修改人 ID：雪花 64 位，对应 Java long';
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
    1
) ON CONFLICT (id) DO NOTHING;

-- 若此前已执行过带「edit_time 触发器」的旧版脚本，可手工清理（无则略过）：
-- DROP TRIGGER IF EXISTS trg_cmn_user_bu ON public.cmn_user;
-- DROP FUNCTION IF EXISTS public.trg_f_cmn_user_touch_edit_time();

-- 角色表：角色名称用于展示，role_code 用于程序内稳定标识（如鉴权点/网关透传）
CREATE TABLE IF NOT EXISTS public.cmn_role (
    id                 BIGINT         NOT NULL,
    role_code          VARCHAR(64)    NOT NULL,
    role_name          VARCHAR(64)    NOT NULL,
    remark             VARCHAR(500)            NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_role PRIMARY KEY (id),
    CONSTRAINT uk_cmn_role_code UNIQUE (role_code),
    CONSTRAINT ck_cmn_role_valid CHECK (valid IN (0, 1))
);

COMMENT ON TABLE public.cmn_role IS '角色表：角色编码 role_code 为稳定业务编码';
COMMENT ON COLUMN public.cmn_role.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.role_code IS '角色编码：如 ROLE_ADMIN、ROLE_OPERATOR';
COMMENT ON COLUMN public.cmn_role.role_name IS '角色名称：用于页面展示';
COMMENT ON COLUMN public.cmn_role.remark IS '备注';
COMMENT ON COLUMN public.cmn_role.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_role.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_role.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_role_valid ON public.cmn_role (valid);

-- 菜单表：menu_code 作为前后端联动标识；支持目录/菜单/按钮三级（按 menu_type 区分）
CREATE TABLE IF NOT EXISTS public.cmn_menu (
    id                 BIGINT         NOT NULL,
    parent_id          BIGINT                 NULL DEFAULT 0,
    menu_code          VARCHAR(64)    NOT NULL,
    menu_name          VARCHAR(64)    NOT NULL,
    menu_type          VARCHAR(16)    NOT NULL DEFAULT 'MENU',
    route_path         VARCHAR(200)            NULL,
    component_path     VARCHAR(200)            NULL,
    icon               VARCHAR(64)             NULL,
    order_no           INTEGER        NOT NULL DEFAULT 0,
    remark             VARCHAR(500)            NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_menu PRIMARY KEY (id),
    CONSTRAINT uk_cmn_menu_code UNIQUE (menu_code),
    CONSTRAINT ck_cmn_menu_valid CHECK (valid IN (0, 1)),
    CONSTRAINT ck_cmn_menu_type CHECK (menu_type IN ('CATALOG', 'MENU', 'BUTTON'))
);

COMMENT ON TABLE public.cmn_menu IS '菜单表：支持目录/菜单/按钮，menu_code 为稳定业务编码';
COMMENT ON COLUMN public.cmn_menu.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu.parent_id IS '父菜单 ID：根节点可为 0 或 NULL';
COMMENT ON COLUMN public.cmn_menu.menu_code IS '菜单编码：如 SYS_USER_LIST、SYS_USER_ADD';
COMMENT ON COLUMN public.cmn_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN public.cmn_menu.menu_type IS '菜单类型：CATALOG=目录，MENU=菜单，BUTTON=按钮';
COMMENT ON COLUMN public.cmn_menu.route_path IS '前端路由路径';
COMMENT ON COLUMN public.cmn_menu.component_path IS '前端组件路径';
COMMENT ON COLUMN public.cmn_menu.icon IS '菜单图标';
COMMENT ON COLUMN public.cmn_menu.order_no IS '同级排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_menu.remark IS '备注';
COMMENT ON COLUMN public.cmn_menu.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_menu.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_menu.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_menu_parent_id ON public.cmn_menu (parent_id);
CREATE INDEX IF NOT EXISTS idx_cmn_menu_valid ON public.cmn_menu (valid);
CREATE INDEX IF NOT EXISTS idx_cmn_menu_type ON public.cmn_menu (menu_type);

-- 角色菜单关联表：relation_code 用于稳定标识一次授权关系（审计/同步场景常用）
CREATE TABLE IF NOT EXISTS public.cmn_role_menu (
    id                 BIGINT         NOT NULL,
    relation_code      VARCHAR(96)    NOT NULL,
    role_id            BIGINT         NOT NULL,
    menu_id            BIGINT         NOT NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_role_menu PRIMARY KEY (id),
    CONSTRAINT uk_cmn_role_menu_code UNIQUE (relation_code),
    CONSTRAINT uk_cmn_role_menu_pair UNIQUE (role_id, menu_id),
    CONSTRAINT ck_cmn_role_menu_valid CHECK (valid IN (0, 1)),
    CONSTRAINT fk_cmn_role_menu_role FOREIGN KEY (role_id) REFERENCES public.cmn_role (id),
    CONSTRAINT fk_cmn_role_menu_menu FOREIGN KEY (menu_id) REFERENCES public.cmn_menu (id)
);

COMMENT ON TABLE public.cmn_role_menu IS '角色菜单关联表：一条记录代表一个角色拥有一个菜单权限';
COMMENT ON COLUMN public.cmn_role_menu.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_menu.relation_code IS '关系编码：如 RM_ROLE_ADMIN_SYS_USER';
COMMENT ON COLUMN public.cmn_role_menu.role_id IS '角色 ID';
COMMENT ON COLUMN public.cmn_role_menu.menu_id IS '菜单 ID';
COMMENT ON COLUMN public.cmn_role_menu.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_role_menu.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_menu.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_menu.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_role_menu.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_role_menu_role_id ON public.cmn_role_menu (role_id);
CREATE INDEX IF NOT EXISTS idx_cmn_role_menu_menu_id ON public.cmn_role_menu (menu_id);
CREATE INDEX IF NOT EXISTS idx_cmn_role_menu_valid ON public.cmn_role_menu (valid);

-- 菜单按钮表：权限粒度建议落到按钮，button_code 作为稳定业务编码
CREATE TABLE IF NOT EXISTS public.cmn_menu_button (
    id                 BIGINT         NOT NULL,
    menu_id            BIGINT         NOT NULL,
    button_code        VARCHAR(64)    NOT NULL,
    button_name        VARCHAR(64)    NOT NULL,
    permission_code    VARCHAR(128)            NULL,
    order_no           INTEGER        NOT NULL DEFAULT 0,
    remark             VARCHAR(500)            NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_menu_button PRIMARY KEY (id),
    CONSTRAINT uk_cmn_menu_button_code UNIQUE (button_code),
    CONSTRAINT uk_cmn_menu_button_menu_code UNIQUE (menu_id, button_code),
    CONSTRAINT ck_cmn_menu_button_valid CHECK (valid IN (0, 1)),
    CONSTRAINT fk_cmn_menu_button_menu FOREIGN KEY (menu_id) REFERENCES public.cmn_menu (id)
);

COMMENT ON TABLE public.cmn_menu_button IS '菜单按钮表：按钮权限实体，角色授权建议绑定到按钮';
COMMENT ON COLUMN public.cmn_menu_button.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.menu_id IS '所属菜单 ID（cmn_menu.id）';
COMMENT ON COLUMN public.cmn_menu_button.button_code IS '按钮编码：如 SYS_USER_ADD、SYS_USER_DELETE';
COMMENT ON COLUMN public.cmn_menu_button.button_name IS '按钮名称：如 新增、删除、导出';
COMMENT ON COLUMN public.cmn_menu_button.permission_code IS '权限点编码（可选）：如 system:user:add';
COMMENT ON COLUMN public.cmn_menu_button.order_no IS '同菜单下排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_menu_button.remark IS '备注';
COMMENT ON COLUMN public.cmn_menu_button.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_menu_button.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_menu_button.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_menu_button_menu_id ON public.cmn_menu_button (menu_id);
CREATE INDEX IF NOT EXISTS idx_cmn_menu_button_valid ON public.cmn_menu_button (valid);

-- 按钮-API 绑定表：描述“点击某按钮会调用哪些 API”，并冗余保存 API 明细（来源 /apis）
CREATE TABLE IF NOT EXISTS public.cmn_button_api (
    id                 BIGINT         NOT NULL,
    relation_code      VARCHAR(96)    NOT NULL,
    button_id          BIGINT         NOT NULL,
    api_code           VARCHAR(64)    NOT NULL,
    api_name           VARCHAR(128)            NULL,
    http_method        VARCHAR(16)    NOT NULL,
    api_path           VARCHAR(256)   NOT NULL,
    service_code       VARCHAR(64)             NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_button_api PRIMARY KEY (id),
    CONSTRAINT uk_cmn_button_api_code UNIQUE (relation_code),
    CONSTRAINT uk_cmn_button_api_pair UNIQUE (button_id, http_method, api_path),
    CONSTRAINT uk_cmn_button_api_api_code UNIQUE (button_id, api_code),
    CONSTRAINT ck_cmn_button_api_valid CHECK (valid IN (0, 1)),
    CONSTRAINT fk_cmn_button_api_button FOREIGN KEY (button_id) REFERENCES public.cmn_menu_button (id)
);

COMMENT ON TABLE public.cmn_button_api IS '按钮-API 绑定表：定义按钮可调用的后端 API 集合（API 明细由 /apis 同步）';
COMMENT ON COLUMN public.cmn_button_api.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.relation_code IS '关系编码：如 BA_SYS_USER_ADD_API_USER_CREATE';
COMMENT ON COLUMN public.cmn_button_api.button_id IS '按钮 ID（cmn_menu_button.id）';
COMMENT ON COLUMN public.cmn_button_api.api_code IS 'API 编码：建议直接使用 /apis 返回的稳定编码';
COMMENT ON COLUMN public.cmn_button_api.api_name IS 'API 名称：用于配置页展示';
COMMENT ON COLUMN public.cmn_button_api.http_method IS 'HTTP 方法：GET/POST/PUT/DELETE 等';
COMMENT ON COLUMN public.cmn_button_api.api_path IS 'API 路径：如 /users/list';
COMMENT ON COLUMN public.cmn_button_api.service_code IS '所属服务编码：如 peach-common-service';
COMMENT ON COLUMN public.cmn_button_api.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_button_api.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_button_api.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_button_api_button_id ON public.cmn_button_api (button_id);
CREATE INDEX IF NOT EXISTS idx_cmn_button_api_method_path ON public.cmn_button_api (http_method, api_path);
CREATE INDEX IF NOT EXISTS idx_cmn_button_api_service_code ON public.cmn_button_api (service_code);
CREATE INDEX IF NOT EXISTS idx_cmn_button_api_valid ON public.cmn_button_api (valid);

-- 角色-按钮授权表：角色权限粒度落在按钮（不直接授权 API）
CREATE TABLE IF NOT EXISTS public.cmn_role_button (
    id                 BIGINT         NOT NULL,
    relation_code      VARCHAR(96)    NOT NULL,
    role_id            BIGINT         NOT NULL,
    button_id          BIGINT         NOT NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_role_button PRIMARY KEY (id),
    CONSTRAINT uk_cmn_role_button_code UNIQUE (relation_code),
    CONSTRAINT uk_cmn_role_button_pair UNIQUE (role_id, button_id),
    CONSTRAINT ck_cmn_role_button_valid CHECK (valid IN (0, 1)),
    CONSTRAINT fk_cmn_role_button_role FOREIGN KEY (role_id) REFERENCES public.cmn_role (id),
    CONSTRAINT fk_cmn_role_button_button FOREIGN KEY (button_id) REFERENCES public.cmn_menu_button (id)
);

COMMENT ON TABLE public.cmn_role_button IS '角色-按钮授权表：角色权限粒度到按钮';
COMMENT ON COLUMN public.cmn_role_button.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.relation_code IS '关系编码：如 RB_ROLE_ADMIN_SYS_USER_ADD';
COMMENT ON COLUMN public.cmn_role_button.role_id IS '角色 ID（cmn_role.id）';
COMMENT ON COLUMN public.cmn_role_button.button_id IS '按钮 ID（cmn_menu_button.id）';
COMMENT ON COLUMN public.cmn_role_button.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_role_button.creator IS '创建人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.editor IS '修改人 ID：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_role_button.edit_time IS '最后更新时间';

CREATE INDEX IF NOT EXISTS idx_cmn_role_button_role_id ON public.cmn_role_button (role_id);
CREATE INDEX IF NOT EXISTS idx_cmn_role_button_button_id ON public.cmn_role_button (button_id);
CREATE INDEX IF NOT EXISTS idx_cmn_role_button_valid ON public.cmn_role_button (valid);

-- 初始化角色（可按实际业务继续扩展）
INSERT INTO public.cmn_role (
    id, role_code, role_name, remark, valid
) VALUES (
    1970000000000000101,
    'ROLE_ADMIN',
    '系统管理员',
    '初始化角色',
    1
) ON CONFLICT (id) DO NOTHING;

-- 初始化菜单
INSERT INTO public.cmn_menu (
    id, parent_id, menu_code, menu_name, menu_type, route_path, component_path, icon, order_no, valid
) VALUES
(
    1970000000000000201,
    0,
    'SYS_MGMT',
    '系统管理',
    'CATALOG',
    '/system',
    NULL,
    'Setting',
    10,
    1
),
(
    1970000000000000202,
    1970000000000000201,
    'SYS_USER',
    '用户管理',
    'MENU',
    '/system/user',
    'views/system/UserView.vue',
    'User',
    11,
    1
)
ON CONFLICT (id) DO NOTHING;

-- 初始化角色菜单关联
INSERT INTO public.cmn_role_menu (
    id, relation_code, role_id, menu_id, valid
) VALUES
(
    1970000000000000301,
    'RM_ROLE_ADMIN_SYS_MGMT',
    1970000000000000101,
    1970000000000000201,
    1
),
(
    1970000000000000302,
    'RM_ROLE_ADMIN_SYS_USER',
    1970000000000000101,
    1970000000000000202,
    1
)
ON CONFLICT (id) DO NOTHING;
