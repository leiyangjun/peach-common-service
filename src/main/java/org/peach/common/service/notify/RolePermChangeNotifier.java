package org.peach.common.service.notify;

/**
 * 角色权限变更通知：全量重建 ROLE_USERS / ROLE_APIS Redis 快照并 Pub/Sub 通知下游刷新。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
public interface RolePermChangeNotifier {

	void afterChange();
}
