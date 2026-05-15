-- 移除 cmn_menu_button 上「仅 button_code 全局唯一」约束，保留 (menu_id, button_code) 组合唯一，
-- 以便多菜单可绑定同一字典码（如 BTN_VIEW）。可重复执行。
ALTER TABLE public.cmn_menu_button DROP CONSTRAINT IF EXISTS uk_cmn_menu_button_code;
