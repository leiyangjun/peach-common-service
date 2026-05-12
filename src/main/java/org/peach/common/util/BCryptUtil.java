package org.peach.common.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 系统用户口令 BCrypt 摘要（与认证服务验密算法一致）。
 */
public final class BCryptUtil {

	private static final int DEFAULT_COST = 10;

	private BCryptUtil() {
	}

	public static String encode(String plainText) {
		if (plainText == null || plainText.isEmpty()) {
			throw new IllegalArgumentException("明文口令不能为空");
		}
		return BCrypt.hashpw(plainText, BCrypt.gensalt(DEFAULT_COST));
	}

	public static boolean matches(String plainText, String encodedHash) {
		if (plainText == null || encodedHash == null || encodedHash.isEmpty()) {
			return false;
		}
		return BCrypt.checkpw(plainText, encodedHash);
	}
}
