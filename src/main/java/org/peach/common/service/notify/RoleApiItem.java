package org.peach.common.service.notify;

/**
 * 角色授权 API 条目：与免鉴权白名单 {@link UnauthApiItem} 一致，使用 method + finalPath。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
public class RoleApiItem {

	private String method;

	private String finalPath;

	public RoleApiItem() {
	}

	public RoleApiItem(String method, String finalPath) {
		this.method = method;
		this.finalPath = finalPath;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getFinalPath() {
		return finalPath;
	}

	public void setFinalPath(String finalPath) {
		this.finalPath = finalPath;
	}
}
