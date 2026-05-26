package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.Application} 生成的对外 VO，不同步时手工改。
 *
 * @author leiyangjun
 */
@Data
public class ApplicationVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：短雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "应用类型：WEB=Web 管理端，MOBILE=移动端，OPENAPI=开放 API 客户端，OTHER=其他")
	private String appType;

	@Schema(description = "应用名称：管理端展示")
	private String appName;

	@Schema(description = "应用编码：全局唯一，如 ADMIN_WEB、PARTNER_OPENAPI")
	private String appCode;

	@Schema(description = "应用描述")
	private String appDesc;

	@Schema(description = "创建人 ID：短雪花 64 位，对应 Java long")
	private Long creator;

	@Schema(description = "修改人 ID：短雪花 64 位，对应 Java long")
	private Long editor;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}
