-- 种子数据：在 init_table.sql 之后执行；可单独重复执行时依赖 ON CONFLICT 幂等
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
-- 初始化菜单：顶层固定为「首页 / 运营 / 报表」三组；其余业务菜单仅调整 parent_id，不改站内 route_path。
-- ID 约定（雪花 64 位，与历史行尽量复用便于已有环境迁移）：
--   197...0100  首页（CATALOG）
--   197...0198  仪表盘（MENU，/dashboard，原「首页」叶子行复用 id）
--   197...0200  运营（CATALOG）
--   197...0201  系统管理（CATALOG，原 id）
--   197...0202～0205、0203  系统管理下功能页（原 id；0203 菜单管理并入系统管理下）
--   197...0206  演示与外链（CATALOG）
--   197...0210～0212  Swagger / 新窗口演示（原 id，父改为 0206）
--   197...0207  报表（CATALOG）
--   197...0208  报表总览（MENU，/report/overview）
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
    'views/dashboard/HomeView.vue',
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
    'views/system/UserView.vue',
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
    'views/system/RoleView.vue',
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
    'views/system/DictView.vue',
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
    'views/system/MenuView.vue',
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
    'views/dashboard/HomeView.vue',
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
    'views/dashboard/HomeView.vue',
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
    'views/dashboard/HomeView.vue',
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
    'views/report/OverviewView.vue',
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

-- 初始化角色菜单关联（ROLE_ADMIN 授权上述全部菜单节点，含目录以便树展示）
INSERT INTO public.cmn_role_menu (
    id, relation_code, role_id, menu_id, valid
) VALUES
(
    1970000000000000289,
    'RM_ROLE_ADMIN_NAV_HOME',
    1970000000000000101,
    1970000000000000100,
    1
),
(
    1970000000000000298,
    'RM_ROLE_ADMIN_HOME',
    1970000000000000101,
    1970000000000000198,
    1
),
(
    1970000000000000290,
    'RM_ROLE_ADMIN_NAV_OPS',
    1970000000000000101,
    1970000000000000200,
    1
),
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
),
(
    1970000000000000304,
    'RM_ROLE_ADMIN_SYS_ROLE',
    1970000000000000101,
    1970000000000000204,
    1
),
(
    1970000000000000305,
    'RM_ROLE_ADMIN_SYS_DICT',
    1970000000000000101,
    1970000000000000205,
    1
),
(
    1970000000000000303,
    'RM_ROLE_ADMIN_SYS_MENU_MGMT',
    1970000000000000101,
    1970000000000000203,
    1
),
(
    1970000000000000291,
    'RM_ROLE_ADMIN_OPS_DEMO_LINKS',
    1970000000000000101,
    1970000000000000206,
    1
),
(
    1970000000000000310,
    'RM_ROLE_ADMIN_DEMO_SWAGGER',
    1970000000000000101,
    1970000000000000210,
    1
),
(
    1970000000000000311,
    'RM_ROLE_ADMIN_DEMO_OPEN_EXT',
    1970000000000000101,
    1970000000000000211,
    1
),
(
    1970000000000000312,
    'RM_ROLE_ADMIN_DEMO_OPEN_INT',
    1970000000000000101,
    1970000000000000212,
    1
),
(
    1970000000000000292,
    'RM_ROLE_ADMIN_NAV_REPORT',
    1970000000000000101,
    1970000000000000207,
    1
),
(
    1970000000000000293,
    'RM_ROLE_ADMIN_REPORT_OVERVIEW',
    1970000000000000101,
    1970000000000000208,
    1
)
ON CONFLICT (id) DO UPDATE SET
    relation_code = EXCLUDED.relation_code,
    role_id = EXCLUDED.role_id,
    menu_id = EXCLUDED.menu_id,
    valid = EXCLUDED.valid;

-- 演示：管理员账号绑定管理员角色
INSERT INTO public.cmn_role_user (
    id, role_id, user_id
) VALUES (
    1970000000000000401,
    1970000000000000101,
    1970000000000000001
) ON CONFLICT (id) DO NOTHING;
