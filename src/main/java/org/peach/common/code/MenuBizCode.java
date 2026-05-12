package org.peach.common.code;

import org.peach.common.mvc.result.code.MessageCode;

/**
 * 菜单管理业务错误码（HTTP 400，消息码段须在 4100–4999）。
 *
 * @author leiyangjun
 */
public enum MenuBizCode implements MessageCode {

	/** 菜单编码不能为空 */
	MENU_CODE_REQUIRED(4110, "菜单编码不能为空"),

	/** 菜单名称不能为空 */
	MENU_NAME_REQUIRED(4111, "菜单名称不能为空"),

	/** 菜单类型不能为空 */
	MENU_TYPE_REQUIRED(4112, "菜单类型不能为空"),

	/** 父菜单不能为当前菜单或其子节点 */
	MENU_PARENT_CYCLE(4113, "父菜单不能为当前菜单或其子节点"),

	/** 父菜单不存在 */
	MENU_PARENT_NOT_FOUND(4114, "父菜单不存在"),

	/** 菜单不存在或已删除 */
	MENU_NOT_FOUND_OR_DELETED(4115, "菜单不存在或已删除"),

	/** 存在子菜单时不允许删除 */
	MENU_HAS_CHILDREN(4116, "请先删除或移走子菜单后再物理删除"),

	/** 删除失败 */
	MENU_DELETE_FAILED(4117, "删除失败");

	private final int code;
	private final String msg;

	MenuBizCode(int code, String msg) {
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
