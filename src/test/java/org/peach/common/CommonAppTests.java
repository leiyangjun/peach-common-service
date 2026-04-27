package org.peach.common;

import org.junit.jupiter.api.Test;

/**
 * 基础单测入口（不拉起 Spring 上下文，避免对数据库与 Mapper 扫描的额外要求）。
 *
 * @author leiyangjun
 */
class CommonAppTests {

	@Test
	void appClassIsLoadable() {
		Class<?> app = CommonApp.class;
		org.junit.jupiter.api.Assertions.assertNotNull(app);
	}
}
