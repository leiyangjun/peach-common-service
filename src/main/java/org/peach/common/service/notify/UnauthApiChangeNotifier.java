package org.peach.common.service.notify;

/**
 * 免鉴权 API 变更通知：全量重建 Redis 快照并 Pub/Sub 通知网关刷新本地缓存。
 */
public interface UnauthApiChangeNotifier {

	void afterChange();
}
