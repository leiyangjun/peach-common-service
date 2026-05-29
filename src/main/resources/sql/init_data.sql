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

(300000000016, 300000000004, 'SYS_MENU_OPS', '运维菜单管理', 'MENU', '/system/menu-ops', NULL, 'Tools', 16, 1),

(300000000017, 300000000004, 'SYS_UNAUTH_API', '免鉴权 API', 'MENU', '/system/unauth-api', NULL, 'Unlock', 17, 1),

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

(400000000035, 300000000009, 700000000005, 'BTN_CANCEL', '取消', 480, 1),

(400000000036, 300000000016, 700000000001, 'BTN_QUERY', '查询', 10, 1),

(400000000037, 300000000016, 700000000002, 'BTN_ADD', '新增', 110, 1),

(400000000038, 300000000016, 700000000003, 'BTN_EDIT', '编辑', 210, 1)

ON CONFLICT (id) DO UPDATE SET

    menu_id = EXCLUDED.menu_id,

    button_id = EXCLUDED.button_id,

    button_code = EXCLUDED.button_code,

    button_name = EXCLUDED.button_name,

    order_no = EXCLUDED.order_no,

    valid = EXCLUDED.valid;


INSERT INTO public.cmn_role_user (id, role_id, user_id) VALUES (600000000001, 200000000001, 100000000001) ON CONFLICT (id) DO NOTHING;

-- 网关 JWT 内置免鉴权：final_path 为客户端原始 Ant 路径（WhitelistFilter 直接匹配；method 禁止 ALL）
DELETE FROM public.cmn_unauth_api WHERE id BETWEEN 610000000001 AND 610000000099;

INSERT INTO public.cmn_unauth_api (id, method, summary, url_path, service_name, is_external, final_path, deletable, valid) VALUES
-- 文档门户
(610000000004, 'GET', 'API 文档门户', NULL, NULL, 1, '/peach-doc-portal/**', 0, 1),
(610000000002, 'GET', '文档门户入口页', NULL, NULL, 1, '/index.html', 0, 1),
-- 网关本机能力（Controller 根路径，无 /peach-gateway 前缀）
(610000000001, 'GET', '网关统一入口', NULL, NULL, 1, '/', 0, 1),
(610000000003, 'GET', '网关路由诊断', NULL, NULL, 1, '/routes', 0, 1),
(610000000005, 'GET', '网关 OpenAPI', NULL, NULL, 1, '/v3/api-docs', 0, 1),
(610000000006, 'GET', '网关 OpenAPI 子路径', NULL, NULL, 1, '/v3/api-docs/**', 0, 1),
(610000000007, 'GET', '网关 Swagger UI', NULL, NULL, 1, '/swagger-ui.html', 0, 1),
(610000000008, 'GET', '网关 Swagger 静态资源', NULL, NULL, 1, '/swagger-ui/**', 0, 1),
(610000000009, 'GET', '网关 Webjars', NULL, NULL, 1, '/webjars/**', 0, 1),
-- peach-auth-service 登录鉴权（/{serviceId}{url_path}）
(610000000010, 'POST', '登录鉴权-滑块挑战', '/admin/auth/login/slider/challenge', 'peach-auth-service', 0, '/peach-auth-service/admin/auth/login/slider/challenge', 0, 1),
(610000000011, 'GET', '登录鉴权-RSA 公钥', '/admin/auth/login/password/public-key', 'peach-auth-service', 0, '/peach-auth-service/admin/auth/login/password/public-key', 0, 1),
(610000000012, 'POST', '登录鉴权-密码登录', '/admin/auth/login/password', 'peach-auth-service', 0, '/peach-auth-service/admin/auth/login/password', 0, 1),
(610000000013, 'POST', '刷新 Token', '/admin/auth/refresh', 'peach-auth-service', 0, '/peach-auth-service/admin/auth/refresh', 0, 1),
-- 各微服务 Swagger（/{serviceId}/... 通配，不含具体服务名）
(610000000020, 'GET', '微服务 Swagger-OpenAPI(admin)', NULL, NULL, 1, '/*/admin/v3/api-docs/**', 0, 1),
(610000000021, 'GET', '微服务 Swagger-UI(admin)', NULL, NULL, 1, '/*/admin/swagger-ui/**', 0, 1),
(610000000022, 'GET', '微服务 Swagger 入口(admin)', NULL, NULL, 1, '/*/admin/swagger-ui.html', 0, 1),
(610000000023, 'GET', '微服务 Swagger-OpenAPI', NULL, NULL, 1, '/*/v3/api-docs/**', 0, 1),
(610000000024, 'GET', '微服务 Swagger-UI', NULL, NULL, 1, '/*/swagger-ui/**', 0, 1),
(610000000025, 'GET', '微服务 Swagger 入口', NULL, NULL, 1, '/*/swagger-ui.html', 0, 1)
ON CONFLICT (final_path) DO UPDATE SET
    method = EXCLUDED.method,
    summary = EXCLUDED.summary,
    url_path = EXCLUDED.url_path,
    service_name = EXCLUDED.service_name,
    is_external = EXCLUDED.is_external,
    deletable = EXCLUDED.deletable,
    valid = EXCLUDED.valid;

