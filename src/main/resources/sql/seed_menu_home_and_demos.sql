-- 已有库增量：首页 + 演示菜单（与 init_table 一致）；未执行过完整 init 时可单独执行本脚本
INSERT INTO public.cmn_menu (
    id, parent_id, menu_code, menu_name, menu_type, route_path, component_path, icon, order_no, valid
) VALUES
(
    1970000000000000198,
    0,
    'HOME',
    '首页',
    'MENU',
    '/dashboard',
    'views/dashboard/HomeView.vue',
    'House',
    5,
    1
),
(
    1970000000000000210,
    0,
    'DEMO_SWAGGER',
    '演示Swagger',
    'MENU',
    'frame://http://127.0.0.1:8090/swagger-ui.html',
    'views/dashboard/HomeView.vue',
    'Document',
    40,
    1
),
(
    1970000000000000211,
    0,
    'DEMO_OPEN_EXT',
    '新窗口外站',
    'MENU',
    'openwindow://http://127.0.0.1:8090/index.html',
    'views/dashboard/HomeView.vue',
    'Link',
    41,
    1
),
(
    1970000000000000212,
    0,
    'DEMO_OPEN_INT',
    '新窗口站内',
    'MENU',
    'openwindow://system/menu',
    'views/dashboard/HomeView.vue',
    'Share',
    42,
    1
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.cmn_role_menu (id, relation_code, role_id, menu_id, valid) VALUES
(1970000000000000298, 'RM_ROLE_ADMIN_HOME', 1970000000000000101, 1970000000000000198, 1),
(1970000000000000310, 'RM_ROLE_ADMIN_DEMO_SWAGGER', 1970000000000000101, 1970000000000000210, 1),
(1970000000000000311, 'RM_ROLE_ADMIN_DEMO_OPEN_EXT', 1970000000000000101, 1970000000000000211, 1),
(1970000000000000312, 'RM_ROLE_ADMIN_DEMO_OPEN_INT', 1970000000000000101, 1970000000000000212, 1)
ON CONFLICT (id) DO NOTHING;
