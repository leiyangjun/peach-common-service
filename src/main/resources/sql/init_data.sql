SET client_encoding = 'UTF8';

SET search_path = public;



INSERT INTO public.cmn_user (id, user_type, username, password, nickname, valid) VALUES (100000000001, 'system', 'admin', '$2a$10$QH4b31LZubhon65yXWmf3uabIBRg2uKaDwsGm5RnqfchpIiVyMQ.O', '系统管理员', 1) ON CONFLICT (id) DO NOTHING;



INSERT INTO public.cmn_role (id, role_code, role_name, remark, valid) VALUES (200000000001, 'ROLE_ADMIN', '系统管理员', '初始化角色', 1) ON CONFLICT (id) DO NOTHING;



INSERT INTO public.cmn_menu (id, parent_id, menu_code, menu_name, menu_type, route_path, component_path, icon, order_no, valid) VALUES

(300000000001, 0, 'NAV_HOME', '首页', 'CATALOG', '/home', NULL, 'House', 5, 1),

(300000000002, 300000000001, 'HOME', '工作台', 'MENU', '/dashboard', NULL, 'House', 10, 1),

(300000000003, 0, 'NAV_OPS', '运营', 'CATALOG', '/ops', NULL, 'Operation', 10, 1),

(300000000004, 300000000003, 'SYS_MGMT', '系统管理', 'CATALOG', '/system', NULL, 'Setting', 10, 1),

(300000000005, 300000000004, 'SYS_USER', '用户管理', 'MENU', '/system/user', NULL, 'User', 11, 1),

(300000000006, 300000000004, 'SYS_ROLE', '角色管理', 'MENU', '/system/role', NULL, 'Lock', 12, 1),

(300000000007, 300000000004, 'SYS_DICT', '码表配置', 'MENU', '/system/dict', NULL, 'Collection', 13, 1),

(300000000008, 300000000004, 'SYS_SCHEDULER', '定时任务管理', 'MENU', '/system/scheduler', NULL, 'Clock', 15, 1),

(300000000009, 300000000004, 'SYS_MENU_MGMT', '菜单管理', 'MENU', '/system/menu', NULL, 'Menu', 14, 1),

(300000000010, 300000000003, 'OPS_DEMO_LINKS', '演示与外链', 'CATALOG', '/ops/demo', NULL, 'Link', 20, 1),

(300000000011, 300000000010, 'DEMO_SWAGGER', '演示Swagger', 'MENU', 'frame://http://127.0.0.1:8090/swagger-ui.html', NULL, 'Document', 41, 1),

(300000000012, 300000000010, 'DEMO_OPEN_EXT', '新窗口外站', 'MENU', 'openwindow://http://127.0.0.1:8090/index.html', NULL, 'Link', 42, 1),

(300000000013, 300000000010, 'DEMO_OPEN_INT', '新窗口站内', 'MENU', 'openwindow://system/menu', NULL, 'Share', 43, 1),

(300000000014, 0, 'NAV_REPORT', '报表', 'CATALOG', '/report', NULL, 'Histogram', 15, 1),

(300000000015, 300000000014, 'REPORT_OVERVIEW', '报表总览', 'MENU', '/report/overview', NULL, 'Histogram', 10, 1)

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



DELETE FROM public.cmn_role_button WHERE id BETWEEN 500000000001 AND 500000000099;

DELETE FROM public.cmn_menu_button WHERE id BETWEEN 400000000001 AND 400000000099;

DELETE FROM public.cmn_button WHERE button_code NOT IN ('BTN_QUERY', 'BTN_ADD', 'BTN_EDIT', 'BTN_DELETE', 'BTN_CANCEL');



INSERT INTO public.cmn_button (id, button_type, button_name, button_code, sort_no, remark) VALUES

(700000000001, 'query', '查询', 'BTN_QUERY', 10, '查看/查询/重置/刷新/日志/列表'),

(700000000002, 'add', '新增', 'BTN_ADD', 110, '新增/子项加号/新建'),

(700000000003, 'update', '编辑', 'BTN_EDIT', 210, '修改/保存/提交/暂停/恢复/状态切换/分配'),

(700000000004, 'delete', '删除', 'BTN_DELETE', 310, '删除/物理删'),

(700000000005, 'update', '取消', 'BTN_CANCEL', 480, '取消/关闭弹窗不保存')

ON CONFLICT (button_code) DO UPDATE SET

    button_type = EXCLUDED.button_type,

    button_name = EXCLUDED.button_name,

    sort_no = EXCLUDED.sort_no,

    remark = EXCLUDED.remark;



INSERT INTO public.cmn_menu_button (id, menu_id, button_id, button_code, button_name, order_no, valid) VALUES

(400000000001, 300000000001, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000002, 300000000002, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000003, 300000000003, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000004, 300000000004, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000005, 300000000010, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000006, 300000000011, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000007, 300000000012, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000008, 300000000013, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000009, 300000000014, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000010, 300000000015, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000011, 300000000005, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000012, 300000000005, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000013, 300000000005, 700000000003, 'BTN_EDIT', '编辑', 210, 1),

(400000000014, 300000000005, 700000000004, 'BTN_DELETE', '删除', 310, 1),

(400000000015, 300000000005, 700000000005, 'BTN_CANCEL', '取消', 480, 1),

(400000000016, 300000000006, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000017, 300000000006, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000018, 300000000006, 700000000003, 'BTN_EDIT', '编辑', 210, 1),

(400000000019, 300000000006, 700000000004, 'BTN_DELETE', '删除', 310, 1),

(400000000020, 300000000006, 700000000005, 'BTN_CANCEL', '取消', 480, 1),

(400000000021, 300000000007, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000022, 300000000007, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000023, 300000000007, 700000000003, 'BTN_EDIT', '编辑', 210, 1),

(400000000024, 300000000007, 700000000004, 'BTN_DELETE', '删除', 310, 1),

(400000000025, 300000000007, 700000000005, 'BTN_CANCEL', '取消', 480, 1),

(400000000026, 300000000008, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000027, 300000000008, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000028, 300000000008, 700000000003, 'BTN_EDIT', '编辑', 210, 1),

(400000000029, 300000000008, 700000000004, 'BTN_DELETE', '删除', 310, 1),

(400000000030, 300000000008, 700000000005, 'BTN_CANCEL', '取消', 480, 1),

(400000000031, 300000000009, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000032, 300000000009, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000033, 300000000009, 700000000003, 'BTN_EDIT', '编辑', 210, 1),

(400000000034, 300000000009, 700000000004, 'BTN_DELETE', '删除', 310, 1),

(400000000035, 300000000009, 700000000005, 'BTN_CANCEL', '取消', 480, 1)

ON CONFLICT (id) DO UPDATE SET

    menu_id = EXCLUDED.menu_id,

    button_id = EXCLUDED.button_id,

    button_code = EXCLUDED.button_code,

    button_name = EXCLUDED.button_name,

    order_no = EXCLUDED.order_no,

    valid = EXCLUDED.valid;


INSERT INTO public.cmn_role_user (id, role_id, user_id) VALUES (600000000001, 200000000001, 100000000001) ON CONFLICT (id) DO NOTHING;

