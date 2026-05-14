-- 种子数据：在 init_table.sql 之后执行；可单独重复执行时依赖 ON CONFLICT 幂等
-- 菜单树（cmn_menu / cmn_menu_button / cmn_role_button）与前端写死侧栏一致，来源：
--   peach-admin-web/src/config/staticSidebarMenus.ts 中 STATIC_SIDEBAR_MENU_TREE
-- （含 menuCode、menuName、menuType、routePath、icon、orderNo、父子层级）。
-- component_path 一律 NULL：与前端约定一致，站内视图由 route_path 解析（见 dynamicMenuRoutes / viewRouteResolver）。
SET client_encoding = 'UTF8';
SET search_path = public;

-- 演示账号：用户名 admin，明文密码 admin（BCrypt）；生产请删除或改密
INSERT INTO public.cmn_user (
    id, user_type, username, password, nickname, valid
) VALUES (
    1970000000000000001,
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
    1970000000000000101,
    'ROLE_ADMIN',
    '系统管理员',
    '初始化角色',
    1
) ON CONFLICT (id) DO NOTHING;

-- =============================================================================
-- 初始化菜单：与 STATIC_SIDEBAR_MENU_TREE 逐项对应（稳定雪花 id，便于已有库迁移/幂等更新）。
--   197...0100  NAV_HOME 首页（CATALOG）
--   197...0198  HOME 工作台（MENU，/dashboard）
--   197...0200  NAV_OPS 运营（CATALOG）
--   197...0201  SYS_MGMT 系统管理（CATALOG）
--   197...0202  SYS_USER；0204 SYS_ROLE；0205 SYS_DICT；0203 SYS_MENU_MGMT
--   197...0206  OPS_DEMO_LINKS 演示与外链（CATALOG）
--   197...0210～0212  DEMO_SWAGGER / DEMO_OPEN_EXT / DEMO_OPEN_INT
--   197...0207  NAV_REPORT；0208 REPORT_OVERVIEW
-- =============================================================================
INSERT INTO public.cmn_menu (
    id, parent_id, menu_code, menu_name, menu_type, route_path, component_path, icon, order_no, valid
) VALUES
(
    1970000000000000100,
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
    1970000000000000198,
    1970000000000000100,
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
    1970000000000000200,
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
    1970000000000000201,
    1970000000000000200,
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
    NULL,
    'User',
    11,
    1
),
(
    1970000000000000204,
    1970000000000000201,
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
    1970000000000000205,
    1970000000000000201,
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
    1970000000000000203,
    1970000000000000201,
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
    1970000000000000206,
    1970000000000000200,
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
    1970000000000000210,
    1970000000000000206,
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
    1970000000000000211,
    1970000000000000206,
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
    1970000000000000212,
    1970000000000000206,
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
    1970000000000000207,
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
    1970000000000000208,
    1970000000000000207,
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
(1970000000000005001, 1970000000000000100, 'ACCESS_NAV_HOME', '访问菜单', 1, 1),
(1970000000000005002, 1970000000000000198, 'ACCESS_HOME', '访问菜单', 1, 1),
(1970000000000005003, 1970000000000000200, 'ACCESS_NAV_OPS', '访问菜单', 1, 1),
(1970000000000005004, 1970000000000000201, 'ACCESS_SYS_MGMT', '访问菜单', 1, 1),
(1970000000000005005, 1970000000000000202, 'ACCESS_SYS_USER', '访问菜单', 1, 1),
(1970000000000005006, 1970000000000000204, 'ACCESS_SYS_ROLE', '访问菜单', 1, 1),
(1970000000000005007, 1970000000000000205, 'ACCESS_SYS_DICT', '访问菜单', 1, 1),
(1970000000000005008, 1970000000000000203, 'ACCESS_SYS_MENU_MGMT', '访问菜单', 1, 1),
(1970000000000005009, 1970000000000000206, 'ACCESS_OPS_DEMO_LINKS', '访问菜单', 1, 1),
(1970000000000005010, 1970000000000000210, 'ACCESS_DEMO_SWAGGER', '访问菜单', 1, 1),
(1970000000000005011, 1970000000000000211, 'ACCESS_DEMO_OPEN_EXT', '访问菜单', 1, 1),
(1970000000000005012, 1970000000000000212, 'ACCESS_DEMO_OPEN_INT', '访问菜单', 1, 1),
(1970000000000005013, 1970000000000000207, 'ACCESS_NAV_REPORT', '访问菜单', 1, 1),
(1970000000000005014, 1970000000000000208, 'ACCESS_REPORT_OVERVIEW', '访问菜单', 1, 1)
ON CONFLICT (id) DO UPDATE SET
    menu_id = EXCLUDED.menu_id,
    button_code = EXCLUDED.button_code,
    button_name = EXCLUDED.button_name,
    order_no = EXCLUDED.order_no,
    valid = EXCLUDED.valid;

INSERT INTO public.cmn_role_button (
    id, role_id, button_id, valid
) VALUES
(1970000000000006101, 1970000000000000101, 1970000000000005001, 1),
(1970000000000006102, 1970000000000000101, 1970000000000005002, 1),
(1970000000000006103, 1970000000000000101, 1970000000000005003, 1),
(1970000000000006104, 1970000000000000101, 1970000000000005004, 1),
(1970000000000006105, 1970000000000000101, 1970000000000005005, 1),
(1970000000000006106, 1970000000000000101, 1970000000000005006, 1),
(1970000000000006107, 1970000000000000101, 1970000000000005007, 1),
(1970000000000006108, 1970000000000000101, 1970000000000005008, 1),
(1970000000000006109, 1970000000000000101, 1970000000000005009, 1),
(1970000000000006110, 1970000000000000101, 1970000000000005010, 1),
(1970000000000006111, 1970000000000000101, 1970000000000005011, 1),
(1970000000000006112, 1970000000000000101, 1970000000000005012, 1),
(1970000000000006113, 1970000000000000101, 1970000000000005013, 1),
(1970000000000006114, 1970000000000000101, 1970000000000005014, 1)
ON CONFLICT (id) DO UPDATE SET
    role_id = EXCLUDED.role_id,
    button_id = EXCLUDED.button_id,
    valid = EXCLUDED.valid;

-- cmn_button_api：按钮与后端 API 的绑定由业务维护或自 /apis 同步，本种子不设默认行。

-- 演示：管理员账号绑定管理员角色
INSERT INTO public.cmn_role_user (
    id, role_id, user_id
) VALUES (
    1970000000000000401,
    1970000000000000101,
    1970000000000000001
) ON CONFLICT (id) DO NOTHING;
