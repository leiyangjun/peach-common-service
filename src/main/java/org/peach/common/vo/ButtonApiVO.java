package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.ButtonApi} 生成的对外 VO，不同步时手工改。
 */
@Data
public class ButtonApiVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "关系编码：如 BA_SYS_USER_ADD_API_USER_CREATE")
	private String relationCode;

	@Schema(description = "按钮 ID（cmn_menu_button.id）")
	private Long buttonId;

	@Schema(description = "API 编码：建议直接使用 /apis 返回的稳定编码")
	private String apiCode;

	@Schema(description = "API 名称：用于配置页展示")
	private String apiName;

	@Schema(description = "HTTP 方法：GET/POST/PUT/DELETE 等")
	private String httpMethod;

	@Schema(description = "API 路径：如 /users/list")
	private String apiPath;

	@Schema(description = "所属服务编码：如 peach-common-service")
	private String serviceCode;

	@Schema(description = "是否有效：1=有效 0=无效（逻辑删除，SMALLINT）")
	private Short valid;

	@Schema(description = "创建人 ID：雪花 64 位，对应 Java long")
	private Long creator;

	@Schema(description = "修改人 ID：雪花 64 位，对应 Java long")
	private Long editor;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}

