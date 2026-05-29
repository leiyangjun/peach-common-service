package org.peach.common.service.notify;

import java.util.List;
import java.util.Map;

/**
 * 角色-API Redis 全量快照：键为稳定业务编码 {@code roleCode}，值为经物化的 API 列表（不含按钮/菜单）。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
public class RolePermApisSnapshot {

	private long revision;

	private String updatedAt;

	/** roleCode → API 列表（method + finalPath） */
	private Map<String, List<RolePermApiItem>> roles;

	public RolePermApisSnapshot() {
	}

	public RolePermApisSnapshot(long revision, String updatedAt, Map<String, List<RolePermApiItem>> roles) {
		this.revision = revision;
		this.updatedAt = updatedAt;
		this.roles = roles;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Map<String, List<RolePermApiItem>> getRoles() {
		return roles;
	}

	public void setRoles(Map<String, List<RolePermApiItem>> roles) {
		this.roles = roles;
	}
}
