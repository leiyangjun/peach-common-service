package org.peach.common.service.notify;

/**
 * 单条匿名规则：HTTP 方法 + 网关 Ant 路径。
 */
public class UnauthApiItem {

	private String method;

	private String finalPath;

	public UnauthApiItem() {
	}

	public UnauthApiItem(String method, String finalPath) {
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
