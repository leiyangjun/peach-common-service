package org.peach.common.web;

import java.util.List;
import java.util.stream.Collectors;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.vo.ServiceVO;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Nacos 服务发现：供前端下拉选择微服务后再拉取 Admin API 目录。
 * <p>
 * 对外完整路径在开启 {@code peach.api.context} 时为 {@code /admin/discovery}。
 * </p>
 *
 * @author leiyangjun
 */
@RestController
@RequestMapping("/discovery")
@Tag(name = "服务发现", description = "Nacos 注册中心可发现的服务列表")
public class DiscoveryController {

	private final DiscoveryClient discoveryClient;

	public DiscoveryController(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}

	@Operation(summary = "注册中心服务列表（下拉）")
	@GetMapping
	public ApiResult<List<ServiceVO>> listServices() {
		List<ServiceVO> serviceVOs = discoveryClient.getServices().stream().map(e -> {
			ServiceVO serviceVO = new ServiceVO();
			serviceVO.setServiceId(e);
			serviceVO.setServiceName(e);
			return serviceVO;
		}).collect(Collectors.toList());
		return ApiResult.ok(serviceVOs);
	}

}
