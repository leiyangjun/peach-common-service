package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 免鉴权 API 管理 VO：校验注解承载入参规则；{@link #finalPath} 唯一性由实体 {@code @Unique} + {@code saveOrUpdate} 完成。
 */
@Data
public class UnauthApiVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
	private Long id;

	@NotBlank(message = "HTTP 方法不能为空")
	@Size(max = 8, message = "HTTP 方法长度不能超过 8")
	@Schema(description = "HTTP 方法，如 GET、POST、ALL")
	private String method;

	@Size(max = 100, message = "摘要长度不能超过 100")
	private String summary;

	@Size(max = 200, message = "路径片段长度不能超过 200")
	private String urlPath;

	@Size(max = 20, message = "服务名长度不能超过 20")
	private String serviceName;

	@NotNull(message = "须指定是否外部 API")
	@Min(0)
	@Max(1)
	@Schema(description = "0=内部微服务 1=外部")
	private Short isExternal;

	@Min(1)
	@Max(2)
	@Schema(description = "访问类型：1=免登录白名单 2=需登录免权限校验；分页可作等值筛选")
	private Short accessType;

	@Size(max = 256, message = "最终路径长度不能超过 256")
	@Schema(description = "网关 Ant 最终路径；内部 API 可由服务端拼接")
	private String finalPath;

	@Schema(description = "是否允许删除：0=否 1=是；分页查询勿传，仅详情/保存使用")
	private Short deletable;

	@Schema(description = "是否启用：1=是 0=否")
	private Short valid;

	private Long creator;

	private Long editor;

	private Date createTime;

	private Date editTime;

}
