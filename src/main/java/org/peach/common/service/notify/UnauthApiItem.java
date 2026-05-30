package org.peach.common.service.notify;

/**
 * 单条匿名规则：HTTP 方法 + 网关 Ant 路径。
 */
public class UnauthApiItem {

	private String method;

	private String finalPath;

	/** 访问类型：1=免登录 2=需登录免权限；缺省按 1 处理 */
	private Short accessType;

	public UnauthApiItem() {
	}

	public UnauthApiItem(String method, String finalPath) {
		this.method = method;
		this.finalPath = finalPath;
	}

	public UnauthApiItem(String method, String finalPath, Short accessType) {
		this.method = method;
		this.finalPath = finalPath;
		this.accessType = accessType;
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

	public Short getAccessType() {
		return accessType;
	}

	public void setAccessType(Short accessType) {
		this.accessType = accessType;
	}
}
