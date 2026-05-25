package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.Button} 生成的对外 VO，不同步时手工改。
 *
 * @author leiyangjun
 */
@Data
public class ButtonVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：短雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "按钮类型：add、query、update、delete、other")
	private String buttonType;

	@Schema(description = "按钮名称（展示）")
	private String buttonName;

	@Schema(description = "按钮编码（全局唯一，与前后端权限标识一致）")
	private String buttonCode;

	@Schema(description = "排序号，越小越靠前")
	private Integer sortNo;

	@Schema(description = "备注")
	private String remark;
}
