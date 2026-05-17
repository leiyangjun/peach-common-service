package org.peach.common;

import org.junit.jupiter.api.Test;

/**
 * 基础单测：仅校验启动类可加载，不启动 Spring 上下文。
 */
class CommonAppTests {

	@Test
	void appClassIsLoadable() {
		Class<?> app = CommonApp.class;
		org.junit.jupiter.api.Assertions.assertNotNull(app);
	}
}
