package org.peach.common.service.notify;

import java.util.List;

/**
 * 网关 JWT 匿名路径全量快照（JSON 存 Redis，与 gateway 侧结构一致）。
 */
public class UnauthApiSnapshot {

	private long revision;

	private String updatedAt;

	private List<UnauthApiItem> items;

	public UnauthApiSnapshot() {
	}

	public UnauthApiSnapshot(long revision, String updatedAt, List<UnauthApiItem> items) {
		this.revision = revision;
		this.updatedAt = updatedAt;
		this.items = items;
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

	public List<UnauthApiItem> getItems() {
		return items;
	}

	public void setItems(List<UnauthApiItem> items) {
		this.items = items;
	}
}
