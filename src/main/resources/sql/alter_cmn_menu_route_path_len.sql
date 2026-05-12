-- 已有库升级：拉长 route_path 以容纳 frame://、openwindow:// 等完整 URL（init_table.sql 已同步为 VARCHAR(1000)）
ALTER TABLE public.cmn_menu
    ALTER COLUMN route_path TYPE VARCHAR(1000);
