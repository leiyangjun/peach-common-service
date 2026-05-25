package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Nacos 可发现的服务项，供前端下拉选择后再拉取该服务的 Admin API 列表。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "Nacos 发现的服务实例 id（一般等于 spring.application.name）")
public class ServiceVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "服务 id，用于负载均衡 URL：http://{serviceId}/...")
	private String serviceId;

	@Schema(description = "服务名称--目前跟ServiceID一致")
	private String serviceName;

}
