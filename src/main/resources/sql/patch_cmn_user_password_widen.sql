-- BCrypt 摘要固定 60 字符；若建表时 password 过短（如 VARCHAR(30)），UPDATE 会被静默截断导致永远登录失败。
-- 在目标库执行本脚本后，请重新执行 UPDATE cmn_user SET password = '<完整60字符哈希>' WHERE username = 'admin';

SET search_path TO public;

ALTER TABLE public.cmn_user
    ALTER COLUMN password TYPE VARCHAR(128);
