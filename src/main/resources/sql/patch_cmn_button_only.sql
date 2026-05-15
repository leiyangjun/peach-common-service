-- 仅部署 cmn_button 字典表及种子（不删其它业务表）；可重复执行
SET client_encoding = 'UTF8';
SET search_path = public;

DROP TABLE IF EXISTS public.cmn_button CASCADE;

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
COMMENT ON COLUMN public.cmn_button.id IS '主键：雪花 64 位，对应 Java long';
COMMENT ON COLUMN public.cmn_button.button_type IS '按钮类型：add、query、update、delete、other';
COMMENT ON COLUMN public.cmn_button.button_name IS '按钮名称（展示）';
COMMENT ON COLUMN public.cmn_button.button_code IS '按钮编码（全局唯一，与前后端权限标识一致）';
COMMENT ON COLUMN public.cmn_button.sort_no IS '排序号，越小越靠前';
COMMENT ON COLUMN public.cmn_button.remark IS '备注';

CREATE INDEX idx_cmn_button_type ON public.cmn_button (button_type);
CREATE INDEX idx_cmn_button_sort ON public.cmn_button (sort_no);

INSERT INTO public.cmn_button (id, button_type, button_name, button_code, sort_no, remark) VALUES
(1970000000000000401, 'query', '查询', 'BTN_QUERY', 10, '列表条件检索'),
(1970000000000000402, 'query', '重置', 'BTN_RESET', 20, '清空查询条件'),
(1970000000000000403, 'query', '刷新', 'BTN_REFRESH', 30, '重新加载数据'),
(1970000000000000404, 'query', '查看', 'BTN_VIEW', 40, '只读详情'),
(1970000000000000405, 'query', '列设置', 'BTN_COLUMN_SETTING', 50, '表格列显隐'),
(1970000000000000411, 'add', '新增', 'BTN_ADD', 110, '打开新增'),
(1970000000000000412, 'add', '导入', 'BTN_IMPORT', 120, '批量导入'),
(1970000000000000421, 'update', '编辑', 'BTN_EDIT', 210, '修改数据'),
(1970000000000000422, 'update', '启用', 'BTN_ENABLE', 220, NULL),
(1970000000000000423, 'update', '禁用', 'BTN_DISABLE', 230, NULL),
(1970000000000000424, 'update', '保存', 'BTN_SAVE', 240, '表单保存/提交'),
(1970000000000000425, 'update', '审核通过', 'BTN_AUDIT_PASS', 250, NULL),
(1970000000000000426, 'update', '审核驳回', 'BTN_AUDIT_REJECT', 260, NULL),
(1970000000000000427, 'update', '分配', 'BTN_ASSIGN', 270, NULL),
(1970000000000000428, 'update', '重置密码', 'BTN_RESET_PASSWORD', 280, '用户管理常见'),
(1970000000000000429, 'update', '解锁账号', 'BTN_UNLOCK', 290, NULL),
(1970000000000000431, 'delete', '删除', 'BTN_DELETE', 310, '单条删除'),
(1970000000000000432, 'delete', '批量删除', 'BTN_BATCH_DELETE', 320, NULL),
(1970000000000000441, 'other', '导出', 'BTN_EXPORT', 410, NULL),
(1970000000000000442, 'other', '下载模板', 'BTN_DOWNLOAD_TEMPLATE', 420, '导入用模板'),
(1970000000000000443, 'other', '复制', 'BTN_COPY', 430, '复制一条'),
(1970000000000000444, 'other', '提交', 'BTN_SUBMIT', 440, '流程/表单提交'),
(1970000000000000445, 'other', '撤回', 'BTN_REVOKE', 450, NULL),
(1970000000000000446, 'other', '下载', 'BTN_DOWNLOAD', 460, '附件下载'),
(1970000000000000447, 'other', '打印', 'BTN_PRINT', 470, NULL),
(1970000000000000448, 'other', '取消', 'BTN_CANCEL', 480, '关闭/不保存'),
(1970000000000000449, 'other', '授权角色', 'BTN_AUTH_ROLE', 490, '用户-角色'),
(1970000000000000450, 'other', '分配菜单', 'BTN_BIND_MENU', 500, '角色-菜单/资源'),
(1970000000000000451, 'other', '分配按钮', 'BTN_BIND_BUTTON', 510, '角色-按钮');
