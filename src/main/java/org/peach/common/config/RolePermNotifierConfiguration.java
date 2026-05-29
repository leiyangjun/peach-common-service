package org.peach.common.config;

import org.peach.common.service.notify.RolePermChangeNotifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 角色权限启动时全量同步 Redis 快照（common-service 须配置 Redis）。
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
@Configuration
public class RolePermNotifierConfiguration {

	@Bean
	ApplicationRunner rolePermStartupSync(RolePermChangeNotifier notifier) {
		return args -> notifier.afterChange();
	}
}
