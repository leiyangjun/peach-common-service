package org.peach.common.code;

import org.peach.common.mvc.result.code.MessageCode;

/**
 * 用户管理业务错误码（HTTP 400，消息码段须在 4100–4999）；与 {@link org.peach.common.mvc.exception.BizException#validWarn(MessageCode)} 配合使用。
 *
 * @author leiyangjun
 */
public enum UserBizCode implements MessageCode {

	/** 用户不存在 */
	USER_NOT_FOUND(4100, "用户不存在"),

	/** 仅系统用户可重置密码 */
	ONLY_SYSTEM_USER_RESET_PWD(4101, "仅系统用户可重置密码"),

	/** 登录名已存在 */
	LOGIN_NAME_EXISTS(4102, "登录名已存在"),

	/** 登录名已被占用 */
	LOGIN_NAME_CONFLICT(4103, "登录名已被占用");

	private final int code;
	private final String msg;

	UserBizCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public int code() {
		return code;
	}

	@Override
	public String msg() {
		return msg;
	}
}
