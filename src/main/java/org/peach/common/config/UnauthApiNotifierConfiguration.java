package org.peach.common.config;

import org.peach.common.service.notify.UnauthApiChangeNotifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 免鉴权 API 启动时全量同步 Redis 快照（common-service 须配置 Redis）。
 */
@Configuration
public class UnauthApiNotifierConfiguration {

	@Bean
	ApplicationRunner unauthApiStartupSync(UnauthApiChangeNotifier notifier) {
		return args -> notifier.afterChange();
	}
}
