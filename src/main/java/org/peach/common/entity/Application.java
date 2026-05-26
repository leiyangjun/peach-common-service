package org.peach.common.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.SearchValue;
import org.peach.common.mybatis.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("cmn_application")
@Schema(description = "应用主数据：按 app_type 区分客户端形态；app_code 为稳定业务编码；service_name 可选对齐 Nacos/网关 serviceId")
public class Application implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：短雪花 64 位，对应 Java long")
	@ID
	private Long id;

	@Schema(description = "应用类型：WEB=Web 管理端，MOBILE=移动端，OPENAPI=开放 API 客户端，OTHER=其他")
	private String appType;

	@Schema(description = "应用名称：管理端展示")
	@SearchValue
	private String appName;

	@Schema(description = "应用编码：全局唯一，如 ADMIN_WEB、PARTNER_OPENAPI")
	@SearchValue
	private String appCode;

	@Schema(description = "应用描述")
	@SearchValue
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