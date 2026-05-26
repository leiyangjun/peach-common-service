-- 创作日期：2026-05-17，作者：leiyangjun — PostgreSQL 12+ 公共库 DDL（与 application 中 DataSource 一致）
-- 执行顺序：先本脚本（纯 DDL），再 init_data.sql（种子数据）；执行前请在目标库 CREATE DATABASE 并 \c 到目标库
-- 策略：DROP TABLE IF EXISTS ... CASCADE 后 CREATE TABLE（破坏性重置，适合开发/空库初始化）
-- 主键策略：短雪花 BIGINT，与 IdUtil.shortSnowId() 一致；不使用外键，关联由应用层维护

SET client_encoding = 'UTF8';
SET search_path = public;

-- ========== 删表（依赖多的先删；各表均 CASCADE，避免残留外键）==========
DROP TABLE IF EXISTS public.cmn_application CASCADE;
DROP TABLE IF EXISTS public.cmn_button CASCADE;
DROP TABLE IF EXISTS public.cmn_button_api CASCADE;
DROP TABLE IF EXISTS public.cmn_role_button CASCADE;
DROP TABLE IF EXISTS public.cmn_menu_button CASCADE;
DROP TABLE IF EXISTS public.cmn_role_menu CASCADE;
DROP TABLE IF EXISTS public.cmn_role_user CASCADE;
DROP TABLE IF EXISTS public.cmn_dict CASCADE;
DROP TABLE IF EXISTS public.cmn_menu CASCADE;
DROP TABLE IF EXISTS public.cmn_role CASCADE;
DROP TABLE IF EXISTS public.cmn_user CASCADE;

-- ========== 建表（父表优先）==========
-- 用户类型：system=系统侧（后台/员工），app=应用端（C 端等）；证件字段可选填，证件号码允许为空
CREATE TABLE public.cmn_user (
    id                 BIGINT         NOT NULL,
    user_type          VARCHAR(16)    NOT NULL DEFAULT 'app',
    username           VARCHAR(64)             NULL,
    password           VARCHAR(128)            NULL, -- BCrypt 须 ≥60 字符；勿缩短此列
    nickname           VARCHAR(64)             NULL,
    real_name          VARCHAR(64)             NULL,
    mobile             VARCHAR(20)             NULL,
    email              VARCHAR(128)            NULL,
    avatar             VARCHAR(512)            NULL,
    gender             SMALLINT                NULL DEFAULT 0,
    cert_type          VARCHAR(32)             NULL,
    cert_no            VARCHAR(128)            NULL,
    last_login_time    TIMESTAMPTZ(3)          NULL,
    last_login_client  VARCHAR(32)             NULL,
    remark             VARCHAR(500)            NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_user PRIMARY KEY (id),
    CONSTRAINT ck_cmn_user_valid CHECK (valid IN (0, 1)),
    CONSTRAINT ck_cmn_user_user_type CHECK (user_type IN ('system', 'app')),
    CONSTRAINT ck_cmn_user_gender CHECK (gender IS NULL OR gender IN (0, 1, 2)),
    CONSTRAINT ck_cmn_user_system_login CHECK (
        user_type <> 'system'
        OR (username IS NOT NULL AND btrim(username) <> '')
    )
);

COMMENT ON TABLE public.cmn_user IS '用户：按 user_type 区分系统侧与 App 端；证件类型/号码用于实名展示（号码可空）';
COMMENT ON COLUMN public.cmn_user.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_user.user_type IS '用户类型：system=系统侧，app=应用端（与 JWT Claim user_type 一致）';
COMMENT ON COLUMN public.cmn_user.username IS '登录名；系统侧必填（由约束保证），app 端可空（手机/三方为主）';
COMMENT ON COLUMN public.cmn_user.password IS '密码摘要；免密或纯三方登录可为空';
COMMENT ON COLUMN public.cmn_user.nickname IS '昵称';
COMMENT ON COLUMN public.cmn_user.real_name IS '真实姓名或对内展示名';
COMMENT ON COLUMN public.cmn_user.mobile IS '手机号';
COMMENT ON COLUMN public.cmn_user.email IS '邮箱';
COMMENT ON COLUMN public.cmn_user.avatar IS '头像地址或对象存储键';
COMMENT ON COLUMN public.cmn_user.gender IS '性别：0 未知，1 男，2 女';
COMMENT ON COLUMN public.cmn_user.cert_type IS '证件类型编码（如 ID_CARD），与业务字典一致；可空';
COMMENT ON COLUMN public.cmn_user.cert_no IS '证件号码；可空';
COMMENT ON COLUMN public.cmn_user.last_login_time IS '最近登录时间';
COMMENT ON COLUMN public.cmn_user.last_login_client IS '最近登录终端（如 ADMIN_WEB、MINI_APP）';
COMMENT ON COLUMN public.cmn_user.remark IS '备注';
COMMENT ON COLUMN public.cmn_user.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_user.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_user.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_user.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_user.edit_time IS '最后更新时间';

CREATE UNIQUE INDEX uk_cmn_user_username ON public.cmn_user (username) WHERE username IS NOT NULL;
CREATE UNIQUE INDEX uk_cmn_user_mobile ON public.cmn_user (mobile) WHERE mobile IS NOT NULL;
CREATE UNIQUE INDEX uk_cmn_user_email ON public.cmn_user (email) WHERE email IS NOT NULL;
CREATE INDEX idx_cmn_user_user_type ON public.cmn_user (user_type);
CREATE INDEX idx_cmn_user_valid ON public.cmn_user (valid);
CREATE INDEX idx_cmn_user_edit_time ON public.cmn_user (edit_time DESC);

-- 角色表：角色名称用于展示，role_code 用于程序内稳定标识（如鉴权点/网关透传）
CREATE TABLE public.cmn_role (
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
COMMENT ON COLUMN public.cmn_role.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.role_code IS '角色编码：如 ROLE_ADMIN、ROLE_OPERATOR';
COMMENT ON COLUMN public.cmn_role.role_name IS '角色名称：用于页面展示';
COMMENT ON COLUMN public.cmn_role.remark IS '备注';
COMMENT ON COLUMN public.cmn_role.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_role.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_role.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_role_valid ON public.cmn_role (valid);
CREATE INDEX idx_cmn_role_edit_time ON public.cmn_role (edit_time DESC);

-- 应用表：逻辑客户端/产品线主数据（管理端 WEB、移动端、开放 API 等）；与菜单/API 权限按应用隔离的演进基础
CREATE TABLE public.cmn_application (
    id                 BIGINT         NOT NULL,
    app_type           VARCHAR(10)    NOT NULL,
    app_name           VARCHAR(30) NOT NULL,
    app_code           VARCHAR(20)    NOT NULL,
    app_desc           VARCHAR(100)            NULL,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_application PRIMARY KEY (id),
    CONSTRAINT uk_cmn_application_code UNIQUE (app_code),
    CONSTRAINT ck_cmn_application_valid CHECK (valid IN (0, 1)),
    CONSTRAINT ck_cmn_application_type CHECK (app_type IN ('ADMIN', 'APP', 'OPENAPI'))
);

COMMENT ON TABLE public.cmn_application IS '应用主数据：按 app_type 区分客户端形态；app_code 为稳定业务编码；service_name 可选对齐 Nacos/网关 serviceId';
COMMENT ON COLUMN public.cmn_application.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_application.app_type IS '应用类型：WEB=Web 管理端，MOBILE=移动端，OPENAPI=开放 API 客户端，OTHER=其他';
COMMENT ON COLUMN public.cmn_application.app_name IS '应用名称：管理端展示';
COMMENT ON COLUMN public.cmn_application.app_code IS '应用编码：全局唯一，如 ADMIN_WEB、PARTNER_OPENAPI';
COMMENT ON COLUMN public.cmn_application.app_desc IS '应用描述';
COMMENT ON COLUMN public.cmn_application.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_application.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_application.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_application.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_application_type ON public.cmn_application (app_type);
CREATE INDEX idx_cmn_application_service_name ON public.cmn_application (service_name) WHERE service_name IS NOT NULL;
CREATE INDEX idx_cmn_application_valid ON public.cmn_application (valid);
CREATE INDEX idx_cmn_application_sort ON public.cmn_application (sort_no);
CREATE INDEX idx_cmn_application_edit_time ON public.cmn_application (edit_time DESC);

-- 码表（字典项）：同一 dict_type 下 dict_value 全局唯一；status=1 启用、0=停用
CREATE TABLE public.cmn_dict (
    id                 BIGINT         NOT NULL,
    dict_type          VARCHAR(50)    NOT NULL,
    dict_label         VARCHAR(100)   NOT NULL,
    dict_value         VARCHAR(100)   NOT NULL,
    sort_no            INTEGER        NOT NULL DEFAULT 0,
    status             SMALLINT       NOT NULL DEFAULT 1,
    remark             VARCHAR(255)            NULL,
    parent_id          BIGINT         NOT NULL DEFAULT 0,
    css_class          VARCHAR(100)            NULL,
    list_class         VARCHAR(100)            NULL,
    is_default         SMALLINT       NOT NULL DEFAULT 0,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_dict PRIMARY KEY (id),
    CONSTRAINT ck_cmn_dict_status CHECK (status IN (0, 1)),
    CONSTRAINT ck_cmn_dict_is_default CHECK (is_default IN (0, 1))
);

COMMENT ON TABLE public.cmn_dict IS '码表项：status 控制启用/停用；物理删除走 DELETE /dict/{id}/hard';
COMMENT ON COLUMN public.cmn_dict.dict_type IS '字典类型/分组编码，如 user_status';
COMMENT ON COLUMN public.cmn_dict.dict_label IS '展示标签';
COMMENT ON COLUMN public.cmn_dict.dict_value IS '存储值，与 dict_type 组合全局唯一';
COMMENT ON COLUMN public.cmn_dict.sort_no IS '同类型下排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_dict.status IS '状态：1=启用 0=停用';
COMMENT ON COLUMN public.cmn_dict.remark IS '备注';
COMMENT ON COLUMN public.cmn_dict.parent_id IS '父级主键，0 表示根节点（树形字典）';
COMMENT ON COLUMN public.cmn_dict.css_class IS '前端附加 CSS 类名';
COMMENT ON COLUMN public.cmn_dict.list_class IS '列表/标签展示样式类（如 Element Plus tag type）';
COMMENT ON COLUMN public.cmn_dict.is_default IS '是否默认项：1=是 0=否';

CREATE UNIQUE INDEX uk_cmn_dict_type_value ON public.cmn_dict (dict_type, dict_value);
CREATE INDEX idx_cmn_dict_type ON public.cmn_dict (dict_type);
CREATE INDEX idx_cmn_dict_status ON public.cmn_dict (status);
CREATE INDEX idx_cmn_dict_parent_id ON public.cmn_dict (parent_id);
CREATE INDEX idx_cmn_dict_edit_time ON public.cmn_dict (edit_time DESC);

-- 角色-用户关联表：多对多；同一用户可绑定多个角色，同一角色下用户不重复
CREATE TABLE public.cmn_role_user (
    id                 BIGINT         NOT NULL,
    role_id            BIGINT         NOT NULL,
    user_id            BIGINT         NOT NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_role_user PRIMARY KEY (id),
    CONSTRAINT uk_cmn_role_user_pair UNIQUE (role_id, user_id)
);

COMMENT ON TABLE public.cmn_role_user IS '角色与用户关联：后台授权场景下将系统用户归入角色';
COMMENT ON COLUMN public.cmn_role_user.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_user.role_id IS '角色 ID（cmn_role.id）';
COMMENT ON COLUMN public.cmn_role_user.user_id IS '用户 ID（cmn_user.id）';
COMMENT ON COLUMN public.cmn_role_user.create_time IS '绑定时间';

CREATE INDEX idx_cmn_role_user_role_id ON public.cmn_role_user (role_id);
CREATE INDEX idx_cmn_role_user_user_id ON public.cmn_role_user (user_id);
CREATE INDEX idx_cmn_role_user_create_time ON public.cmn_role_user (create_time DESC);

-- 菜单表：menu_code 作为前后端联动标识；支持目录/菜单/按钮三级（按 menu_type 区分）；route_path 支持 frame://、openwindow:// 等长 URL
CREATE TABLE public.cmn_menu (
    id                 BIGINT         NOT NULL,
    parent_id          BIGINT                 NULL DEFAULT 0,
    menu_code          VARCHAR(64)    NOT NULL,
    menu_name          VARCHAR(64)    NOT NULL,
    menu_type          VARCHAR(16)    NOT NULL DEFAULT 'MENU',
    route_path         VARCHAR(1000)           NULL,
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
COMMENT ON COLUMN public.cmn_menu.id IS '主键：短雪花 64 位，对应 Java long';
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
COMMENT ON COLUMN public.cmn_menu.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_menu.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_menu_parent_id ON public.cmn_menu (parent_id);
CREATE INDEX idx_cmn_menu_valid ON public.cmn_menu (valid);
CREATE INDEX idx_cmn_menu_type ON public.cmn_menu (menu_type);
CREATE INDEX idx_cmn_menu_edit_time ON public.cmn_menu (edit_time DESC);

-- 全局按钮字典：菜单绑定时从本表选择；button_type=add|query|update|delete|other；不含 valid 字段
CREATE TABLE public.cmn_button (
    id                 BIGINT         NOT NULL,
    button_type        VARCHAR(16)    NOT NULL,
    button_name        VARCHAR(64)    NOT NULL,
    button_code        VARCHAR(64)    NOT NULL,
    sort_no            INTEGER        NOT NULL DEFAULT 0,
    remark             VARCHAR(500)            NULL,
    CONSTRAINT pk_cmn_button PRIMARY KEY (id),
    CONSTRAINT uk_cmn_button_code UNIQUE (button_code),
    CONSTRAINT ck_cmn_button_type CHECK (button_type IN ('add', 'query', 'update', 'delete', 'other'))
);

COMMENT ON TABLE public.cmn_button IS '全局按钮字典：供菜单绑定选择；类型 add=新增 query=查询 update=修改 delete=删除 other=其他';
COMMENT ON COLUMN public.cmn_button.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button.button_type IS '按钮类型：add、query、update、delete、other';
COMMENT ON COLUMN public.cmn_button.button_name IS '按钮名称（展示）';
COMMENT ON COLUMN public.cmn_button.button_code IS '按钮编码（全局唯一，与前后端权限标识一致）';
COMMENT ON COLUMN public.cmn_button.sort_no IS '排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_button.remark IS '备注';

CREATE INDEX idx_cmn_button_type ON public.cmn_button (button_type);
CREATE INDEX idx_cmn_button_sort ON public.cmn_button (sort_no);

-- 菜单按钮表：权限粒度建议落到按钮，button_code 作为稳定业务编码
CREATE TABLE public.cmn_menu_button (
    id                 BIGINT         NOT NULL,
    menu_id            BIGINT         NOT NULL,
    button_id            BIGINT         NOT NULL,
    button_code        VARCHAR(64)    NOT NULL,
    button_name        VARCHAR(64)    NOT NULL,
    order_no           INTEGER       ,
    remark             VARCHAR(500)            NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_menu_button PRIMARY KEY (id),
    CONSTRAINT uk_cmn_menu_button_menu_code UNIQUE (menu_id, button_code),
    CONSTRAINT ck_cmn_menu_button_valid CHECK (valid IN (0, 1))
);

COMMENT ON TABLE public.cmn_menu_button IS '菜单按钮表：按钮权限实体，角色授权建议绑定到按钮';
COMMENT ON COLUMN public.cmn_menu_button.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.menu_id IS '所属菜单 ID（cmn_menu.id）';
COMMENT ON COLUMN public.cmn_menu_button.button_code IS '按钮编码：如 SYS_USER_ADD、SYS_USER_DELETE';
COMMENT ON COLUMN public.cmn_menu_button.button_name IS '按钮名称：如 新增、删除、导出';
COMMENT ON COLUMN public.cmn_menu_button.order_no IS '同菜单下排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_menu_button.remark IS '备注';
COMMENT ON COLUMN public.cmn_menu_button.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_menu_button.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_menu_button.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_menu_button.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_menu_button_menu_id ON public.cmn_menu_button (menu_id);
CREATE INDEX idx_cmn_menu_button_valid ON public.cmn_menu_button (valid);
CREATE INDEX idx_cmn_menu_button_edit_time ON public.cmn_menu_button (edit_time DESC);

-- 按钮-API 绑定表：字段与 org.peach.common.mvc.api.vo.ApiMeta 对齐（另保留 api_code 作稳定绑定键）
CREATE TABLE public.cmn_button_api (
    id                 BIGINT         NOT NULL,
    menu_id          BIGINT         NOT NULL,
    button_id          BIGINT         NOT NULL,
    method             VARCHAR(16)    NOT NULL,
    summary            VARCHAR(256)            NULL,
    description        VARCHAR(2000)           NULL,
    api_desc           VARCHAR(256)            NULL,
    url_path           VARCHAR(512)   NOT NULL,
    path_pattern       VARCHAR(512)            NULL,
    service_name       VARCHAR(128)            NULL,
    api_type           VARCHAR(32)             NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_button_api PRIMARY KEY (id),
    CONSTRAINT uk_cmn_button_api_pair UNIQUE (button_id, method, url_path),
    CONSTRAINT ck_cmn_button_api_valid CHECK (valid IN (0, 1))
);

COMMENT ON TABLE public.cmn_button_api IS '按钮-API 绑定表：定义按钮可调用的后端 API 集合（字段与 ApiMeta 一致）';
COMMENT ON COLUMN public.cmn_button_api.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.button_id IS '按钮 ID（cmn_button.id）';
COMMENT ON COLUMN public.cmn_button_api.method IS 'HTTP 方法，对应 ApiMeta.method，如 GET、POST、ALL';
COMMENT ON COLUMN public.cmn_button_api.summary IS '接口摘要，对应 ApiMeta.summary';
COMMENT ON COLUMN public.cmn_button_api.description IS '接口详细说明，对应 ApiMeta.description';
COMMENT ON COLUMN public.cmn_button_api.api_desc IS '展示用简短说明，对应 ApiMeta.apiDesc';
COMMENT ON COLUMN public.cmn_button_api.url_path IS '路径模板，对应 ApiMeta.urlPath';
COMMENT ON COLUMN public.cmn_button_api.path_pattern IS 'PathPattern 表达式，对应 ApiMeta.pathPattern';
COMMENT ON COLUMN public.cmn_button_api.service_name IS '所属服务名，对应 ApiMeta.serviceName';
COMMENT ON COLUMN public.cmn_button_api.api_type IS '接口形态 admin/app/openapi，对应 ApiMeta.apiType';
COMMENT ON COLUMN public.cmn_button_api.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_button_api.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button_api.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_button_api.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_button_api_button_id ON public.cmn_button_api (button_id);
CREATE INDEX idx_cmn_button_api_method_path ON public.cmn_button_api (method, url_path);
CREATE INDEX idx_cmn_button_api_service_name ON public.cmn_button_api (service_name);
CREATE INDEX idx_cmn_button_api_valid ON public.cmn_button_api (valid);
CREATE INDEX idx_cmn_button_api_edit_time ON public.cmn_button_api (edit_time DESC);

-- 角色-按钮授权表：角色权限粒度落在按钮（不直接授权 API）
CREATE TABLE public.cmn_role_button (
    id                 BIGINT         NOT NULL,
    menu_id            BIGINT         NOT NULL,
    role_id            BIGINT         NOT NULL,
    button_id          BIGINT         NOT NULL,
    valid              SMALLINT       NOT NULL DEFAULT 1,
    creator            BIGINT                  NULL,
    editor             BIGINT                  NULL,
    create_time        TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edit_time          TIMESTAMPTZ(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cmn_role_button PRIMARY KEY (id),
    CONSTRAINT ck_cmn_role_button_valid CHECK (valid IN (0, 1))
);

COMMENT ON TABLE public.cmn_role_button IS '角色-按钮授权表：角色权限粒度到按钮';
COMMENT ON COLUMN public.cmn_role_button.id IS '主键：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.role_id IS '角色 ID（cmn_role.id）';
COMMENT ON COLUMN public.cmn_role_button.button_id IS '按钮 ID（cmn_button.id）';
COMMENT ON COLUMN public.cmn_role_button.valid IS '是否有效：1=有效 0=无效（逻辑删除，SMALLINT）';
COMMENT ON COLUMN public.cmn_role_button.creator IS '创建人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.editor IS '修改人 ID：短雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_role_button.create_time IS '创建时间';
COMMENT ON COLUMN public.cmn_role_button.edit_time IS '最后更新时间';

CREATE INDEX idx_cmn_role_button_role_id ON public.cmn_role_button (role_id);
CREATE INDEX idx_cmn_role_button_button_id ON public.cmn_role_button (button_id);
CREATE INDEX idx_cmn_role_button_valid ON public.cmn_role_button (valid);
CREATE INDEX idx_cmn_role_button_edit_time ON public.cmn_role_button (edit_time DESC);
