-- 种子数据：在 init_table.sql 之后执行；可单独重复执行时依赖 ON CONFLICT 幂等
-- 主键策略：短雪花固定段（12 位十进制，无前导 0），与 IdUtil.shortSnowId() 量级一致，便于开发/演示环境稳定引用。
-- 菜单树（cmn_menu / cmn_menu_button / cmn_role_button）与前端写死侧栏一致，来源：
--   peach-admin-web/src/config/staticSidebarMenus.ts 中 STATIC_SIDEBAR_MENU_TREE
-- （含 menuCode、menuName、menuType、routePath、icon、orderNo、父子层级）。
-- component_path 一律 NULL：与前端约定一致，站内视图由 route_path 解析（见 dynamicMenuRoutes / viewRouteResolver）。
SET client_encoding = 'UTF8';
SET search_path = public;

-- =============================================================================
-- ID 段约定（12 位短雪花固定种子，禁止前导 0 以免 PostgreSQL 八进制解析）
--   100000000001        admin 用户
--   200000000001        ROLE_ADMIN
--   300000000001～015   菜单（含 SYS_SCHEDULER 定时任务管理）
--   400000000001～015   菜单访问按钮
--   500000000001～015   角色-按钮授权
--   600000000001        用户-角色绑定
--   700000000001～051   全局按钮字典 cmn_button
-- =============================================================================

-- 演示账号：用户名 admin，明文密码 admin（BCrypt）；生产请删除或改密
INSERT INTO public.cmn_user (
    id, user_type, username, password, nickname, valid
) VALUES (
    100000000001,
    'system',
    'admin',
    '$2a$10$QH4b31LZubhon65yXWmf3uabIBRg2uKaDwsGm5RnqfchpIiVyMQ.O',
    '系统管理员',
    1
) ON CONFLICT (id) DO NOTHING;

-- 初始化角色（可按实际业务继续扩展）
INSERT INTO public.cmn_role (
    id, role_code, role_name, remark, valid
) VALUES (
    200000000001,
    'ROLE_ADMIN',
    '系统管理员',
    '初始化角色',
    1
) ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- 初始化菜单：与 STATIC_SIDEBAR_MENU_TREE 逐项对应
--   300000000001  NAV_HOME 首页（CATALOG）
--   300000000002  HOME 工作台（MENU，/dashboard）
--   300000000003  NAV_OPS 运营（CATALOG）
--   300000000004  SYS_MGMT 系统管理（CATALOG）
--   300000000005  SYS_USER；006 SYS_ROLE；007 SYS_DICT；009 SYS_MENU_MGMT；008 SYS_SCHEDULER
--   300000000010  OPS_DEMO_LINKS 演示与外链（CATALOG）
--   300000000011～013  DEMO_SWAGGER / DEMO_OPEN_EXT / DEMO_OPEN_INT
--   300000000014  NAV_REPORT；015 REPORT_OVERVIEW
-- =============================================================================
INSERT INTO public.cmn_menu (
    id, parent_id, menu_code, menu_name, menu_type, route_path, component_path, icon, order_no, valid
) VALUES
(
    300000000001,
    0,
    'NAV_HOME',
    '首页',
    'CATALOG',
    '/home',
    NULL,
    'House',
    5,
    1
),
(
    300000000002,
    300000000001,
    'HOME',
    '工作台',
    'MENU',
    '/dashboard',
    NULL,
    'House',
    10,
    1
),
(
    300000000003,
    0,
    'NAV_OPS',
    '运营',
    'CATALOG',
    '/ops',
    NULL,
    'Operation',
    10,
    1
),
(
    300000000004,
    300000000003,
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
    300000000005,
    300000000004,
    'SYS_USER',
    '用户管理',
    'MENU',
    '/system/user',
    NULL,
    'User',
    11,
    1
),
(
    300000000006,
    300000000004,
    'SYS_ROLE',
    '角色管理',
    'MENU',
    '/system/role',
    NULL,
    'Lock',
    12,
    1
),
(
    300000000007,
    300000000004,
    'SYS_DICT',
    '码表配置',
    'MENU',
    '/system/dict',
    NULL,
    'Collection',
    13,
    1
),
(
    300000000008,
    300000000004,
    'SYS_SCHEDULER',
    '定时任务管理',
    'MENU',
    '/system/scheduler',
    NULL,
    'Clock',
    15,
    1
),
(
    300000000009,
    300000000004,
    'SYS_MENU_MGMT',
    '菜单管理',
    'MENU',
    '/system/menu',
    NULL,
    'Menu',
    14,
    1
),
(
    300000000010,
    300000000003,
    'OPS_DEMO_LINKS',
    '演示与外链',
    'CATALOG',
    '/ops/demo',
    NULL,
    'Link',
    20,
    1
),
(
    300000000011,
    300000000010,
    'DEMO_SWAGGER',
    '演示Swagger',
    'MENU',
    'frame://http://127.0.0.1:8090/swagger-ui.html',
    NULL,
    'Document',
    41,
    1
),
(
    300000000012,
    300000000010,
    'DEMO_OPEN_EXT',
    '新窗口外站',
    'MENU',
    'openwindow://http://127.0.0.1:8090/index.html',
    NULL,
    'Link',
    42,
    1
),
(
    300000000013,
    300000000010,
    'DEMO_OPEN_INT',
    '新窗口站内',
    'MENU',
    'openwindow://system/menu',
    NULL,
    'Share',
    43,
    1
),
(
    300000000014,
    0,
    'NAV_REPORT',
    '报表',
    'CATALOG',
    '/report',
    NULL,
    'Histogram',
    15,
    1
),
(
    300000000015,
    300000000014,
    'REPORT_OVERVIEW',
    '报表总览',
    'MENU',
    '/report/overview',
    NULL,
    'Histogram',
    10,
    1
)
ON CONFLICT (id) DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    menu_code = EXCLUDED.menu_code,
    menu_name = EXCLUDED.menu_name,
    menu_type = EXCLUDED.menu_type,
    route_path = EXCLUDED.route_path,
    component_path = EXCLUDED.component_path,
    icon = EXCLUDED.icon,
    order_no = EXCLUDED.order_no,
    valid = EXCLUDED.valid;

-- 原 cmn_role_menu 已废弃：以「每菜单一条访问按钮 + cmn_role_button」表达角色可见范围（ROLE_ADMIN 全量）
INSERT INTO public.cmn_menu_button (
    id, menu_id, button_code, button_name, order_no, valid
) VALUES
(400000000001, 300000000001, 'ACCESS_NAV_HOME', '访问菜单', 1, 1),
(400000000002, 300000000002, 'ACCESS_HOME', '访问菜单', 1, 1),
(400000000003, 300000000003, 'ACCESS_NAV_OPS', '访问菜单', 1, 1),
(400000000004, 300000000004, 'ACCESS_SYS_MGMT', '访问菜单', 1, 1),
(400000000005, 300000000005, 'ACCESS_SYS_USER', '访问菜单', 1, 1),
(400000000006, 300000000006, 'ACCESS_SYS_ROLE', '访问菜单', 1, 1),
(400000000007, 300000000007, 'ACCESS_SYS_DICT', '访问菜单', 1, 1),
(400000000008, 300000000008, 'ACCESS_SYS_SCHEDULER', '访问菜单', 1, 1),
(400000000009, 300000000009, 'ACCESS_SYS_MENU_MGMT', '访问菜单', 1, 1),
(400000000010, 300000000010, 'ACCESS_OPS_DEMO_LINKS', '访问菜单', 1, 1),
(400000000011, 300000000011, 'ACCESS_DEMO_SWAGGER', '访问菜单', 1, 1),
(400000000012, 300000000012, 'ACCESS_DEMO_OPEN_EXT', '访问菜单', 1, 1),
(400000000013, 300000000013, 'ACCESS_DEMO_OPEN_INT', '访问菜单', 1, 1),
(400000000014, 300000000014, 'ACCESS_NAV_REPORT', '访问菜单', 1, 1),
(400000000015, 300000000015, 'ACCESS_REPORT_OVERVIEW', '访问菜单', 1, 1)
ON CONFLICT (id) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    button_code = EXCLUDED.button_code,
    button_name = EXCLUDED.button_name,
    order_no = EXCLUDED.order_no,
    valid = EXCLUDED.valid;

INSERT INTO public.cmn_role_button (
    id, role_id, button_id, valid
) VALUES
(500000000001, 200000000001, 400000000001, 1),
(500000000002, 200000000001, 400000000002, 1),
(500000000003, 200000000001, 400000000003, 1),
(500000000004, 200000000001, 400000000004, 1),
(500000000005, 200000000001, 400000000005, 1),
(500000000006, 200000000001, 400000000006, 1),
(500000000007, 200000000001, 400000000007, 1),
(500000000008, 200000000001, 400000000008, 1),
(500000000009, 200000000001, 400000000009, 1),
(500000000010, 200000000001, 400000000010, 1),
(500000000011, 200000000001, 400000000011, 1),
(500000000012, 200000000001, 400000000012, 1),
(500000000013, 200000000001, 400000000013, 1),
(500000000014, 200000000001, 400000000014, 1),
(500000000015, 200000000001, 400000000015, 1)
ON CONFLICT (id) DO UPDATE SET
    role_id = EXCLUDED.role_id,
    button_id = EXCLUDED.button_id,
    valid = EXCLUDED.valid;

-- 全局按钮字典（常见操作；供菜单绑定选择）
INSERT INTO public.cmn_button (id, button_type, button_name, button_code, sort_no, remark) VALUES
-- query
(700000000001, 'query', '查询', 'BTN_QUERY', 10, '列表条件检索'),
(700000000002, 'query', '重置', 'BTN_RESET', 20, '清空查询条件'),
(700000000003, 'query', '刷新', 'BTN_REFRESH', 30, '重新加载数据'),
(700000000004, 'query', '查看', 'BTN_VIEW', 40, '只读详情'),
(700000000005, 'query', '列设置', 'BTN_COLUMN_SETTING', 50, '表格列显隐'),
-- add
(700000000011, 'add', '新增', 'BTN_ADD', 110, '打开新增'),
(700000000012, 'add', '导入', 'BTN_IMPORT', 120, '批量导入'),
-- update
(700000000021, 'update', '编辑', 'BTN_EDIT', 210, '修改数据'),
(700000000022, 'update', '启用', 'BTN_ENABLE', 220, NULL),
(700000000023, 'update', '禁用', 'BTN_DISABLE', 230, NULL),
(700000000024, 'update', '保存', 'BTN_SAVE', 240, '表单保存/提交'),
(700000000025, 'update', '审核通过', 'BTN_AUDIT_PASS', 250, NULL),
(700000000026, 'update', '审核驳回', 'BTN_AUDIT_REJECT', 260, NULL),
(700000000027, 'update', '分配', 'BTN_ASSIGN', 270, NULL),
(700000000028, 'update', '重置密码', 'BTN_RESET_PASSWORD', 280, '用户管理常见'),
(700000000029, 'update', '解锁账号', 'BTN_UNLOCK', 290, NULL),
-- delete
(700000000031, 'delete', '删除', 'BTN_DELETE', 310, '单条删除'),
(700000000032, 'delete', '批量删除', 'BTN_BATCH_DELETE', 320, NULL),
-- other
(700000000041, 'other', '导出', 'BTN_EXPORT', 410, NULL),
(700000000042, 'other', '下载模板', 'BTN_DOWNLOAD_TEMPLATE', 420, '导入用模板'),
(700000000043, 'other', '复制', 'BTN_COPY', 430, '复制一条'),
(700000000044, 'other', '提交', 'BTN_SUBMIT', 440, '流程/表单提交'),
(700000000045, 'other', '撤回', 'BTN_REVOKE', 450, NULL),
(700000000046, 'other', '下载', 'BTN_DOWNLOAD', 460, '附件下载'),
(700000000047, 'other', '打印', 'BTN_PRINT', 470, NULL),
(700000000048, 'other', '取消', 'BTN_CANCEL', 480, '关闭/不保存'),
(700000000049, 'other', '授权角色', 'BTN_AUTH_ROLE', 490, '用户-角色'),
(700000000050, 'other', '分配菜单', 'BTN_BIND_MENU', 500, '角色-菜单/资源'),
(700000000051, 'other', '分配按钮', 'BTN_BIND_BUTTON', 510, '角色-按钮')
ON CONFLICT (button_code) DO NOTHING;

-- cmn_button_api：按钮与后端 API 的绑定由业务维护或自 /apis 同步，本种子不设默认行。

-- 演示：管理员账号绑定管理员角色
INSERT INTO public.cmn_role_user (
    id, role_id, user_id
) VALUES (
    600000000001,
    200000000001,
    100000000001
) ON CONFLICT (id) DO NOTHING;
