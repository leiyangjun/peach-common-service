package org.peach.common;

import org.peach.common.mybatis.generator.GeneratorUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Peach 基础服务启动入口。
 * <p>
 * <b>安全分工：</b>本服务仅承载业务 API，不再提供认证能力；
 * <b>API 网关</b>负责校验令牌有效性，并将用户信息转发给下游。业务 REST（如 {@code /api/**}、{@code /user/**}）不在此验 JWT，
 * 由部署与网关统一收口。
 * </p>
 * <p>
 * 业务数据能力（用户、角色等）在本工程中迭代；表结构与具体接口以代码为准。
 * </p>
 *
 * @author leiyangjun
 */
@SpringBootApplication
public class CommonApp {

	public static void main(String[] args) {
		//SpringApplication.run(CommonApp.class, args);
		GeneratorUtil.generateAll(
				"jdbc:postgresql://192.168.99.100:5432/peach_common?currentSchema=public",
				"postgres",
				"postgres",
				"org.postgresql.Driver",
				"cmn_user",
				"cmn_role",
				"cmn_menu",
				"cmn_role_menu",
				"cmn_menu_button",
				"cmn_button_api",
				"cmn_role_button");
	}
}
