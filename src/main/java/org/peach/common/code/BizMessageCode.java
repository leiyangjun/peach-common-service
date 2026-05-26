package org.peach.common.code;

import org.peach.common.mvc.result.code.MessageCode;

/**
 * 基础业务服务统一业务消息码入口（HTTP 400 语义，号段 4100–4999），与
 * {@link org.peach.common.mvc.exception.BizException#validWarn(MessageCode)} 配合使用。
 * <p>
 * 按业务域划分子枚举，避免散落多个 {@code *BizCode} 文件。
 * </p>
 *
 * @author leiyangjun
 */
public final class BizMessageCode {

	private BizMessageCode() {}

	// ---------- 用户域 ----------

	/**
	 * 用户管理（号段 4100–4109）。
	 */
	public enum User implements MessageCode {

		/** 用户不存在 */
		USER_NOT_FOUND(4100, "用户不存在"),

		/** 仅系统用户可重置密码 */
		ONLY_SYSTEM_USER_RESET_PWD(4101, "仅系统用户可重置密码"),

		/** 登录名已存在 */
		LOGIN_NAME_EXISTS(4102, "登录名已存在"),

		/** 登录名已被占用 */
		LOGIN_NAME_CONFLICT(4103, "登录名已被占用"),

		/** 仅系统用户可物理删除 */
		ONLY_SYSTEM_USER_PHYSICAL_DELETE(4104, "仅系统用户支持物理删除"),

		/** 物理删除未生效（如并发删除） */
		USER_HARD_DELETE_FAILED(4105, "物理删除失败，请刷新后重试");

		private final int code;
		private final String msg;

		User(int code, String msg) {
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

	// ---------- 菜单域 ----------

	/**
	 * 菜单管理（号段 4110–4119）。
	 */
	public enum Menu implements MessageCode {

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
		MENU_DELETE_FAILED(4117, "删除失败"),

		/** 运维接口不支持的菜单类型 */
		MENU_OPS_NOT_CATALOG(4118, "运维仅支持目录或菜单类型"),

		/** 查询按钮字典缺失 */
		MENU_BTN_QUERY_NOT_FOUND(4119, "系统未配置查询按钮 BTN_QUERY"),

		/** 存在子节点时不允许改为菜单类型 */
		MENU_OPS_CATALOG_HAS_CHILDREN(4120, "存在子菜单时不能改为菜单类型"),

		/** 菜单类型须挂在目录下 */
		MENU_OPS_MENU_PARENT_MUST_CATALOG(4121, "菜单类型必须选择目录作为上级"),

		/** 运维新建仅允许目录 */
		MENU_OPS_CREATE_ONLY_CATALOG(4122, "运维新建仅支持目录类型"),

		/** 运维删除仅允许目录 */
		MENU_OPS_DELETE_NOT_CATALOG(4123, "仅目录可删除，菜单不可删除"),
		
		/** 菜单只能移动到目录下不能挂到菜单下 */
		MENU_CATALOG_TYPE(4123, "菜单只能移动到目录下不能挂到菜单下");

		private final int code;
		private final String msg;

		Menu(int code, String msg) {
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

	// ---------- 角色域 ----------

	/**
	 * 角色管理（号段 4120–4129）。
	 */
	public enum Role implements MessageCode {

		/** 角色不存在 */
		ROLE_NOT_FOUND(4120, "角色不存在"),

		/** 角色编码已存在 */
		ROLE_CODE_EXISTS(4121, "角色编码已存在"),

		/** 角色编码冲突 */
		ROLE_CODE_CONFLICT(4122, "角色编码已被占用"),

		/** 角色编码不能为空 */
		ROLE_CODE_REQUIRED(4123, "角色编码不能为空"),

		/** 角色名称不能为空 */
		ROLE_NAME_REQUIRED(4124, "角色名称不能为空"),

		/** 物理删除失败 */
		ROLE_HARD_DELETE_FAILED(4125, "物理删除失败，请刷新后重试"),

		/** 绑定的用户不存在 */
		ROLE_BIND_USER_NOT_FOUND(4126, "绑定的用户不存在"),
		
		/** 用户没有菜单按钮权限 */
		ROLE_NOT_AUTH(4127, "您没有权限访问系统，请联系管理员！");

		private final int code;
		private final String msg;

		Role(int code, String msg) {
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

	// ---------- 码表域 ----------

	/**
	 * 码表配置（号段 4130–4139）。
	 */
	public enum Dict implements MessageCode {

		/** 码表项不存在 */
		DICT_NOT_FOUND(4130, "码表项不存在");

		private final int code;
		private final String msg;

		Dict(int code, String msg) {
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
}
