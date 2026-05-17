package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.TableName;

/**
 * 全局按钮字典表 {@code cmn_button} 实体；供菜单多选绑定，无逻辑删除列。
 *
 * @author leiyangjun
 */
@Data
@TableName("cmn_button")
@Schema(description = "全局按钮字典：菜单绑定时仅允许从此表多选")
public class ButtonDict implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
	@ID
	private Long id;

	@Schema(description = "按钮类型：add/query/update/delete/other")
	private String buttonType;

	@Schema(description = "按钮名称（展示）")
	private String buttonName;

	@Schema(description = "按钮编码，全局唯一，如 BTN_VIEW")
	private String buttonCode;

	@Schema(description = "排序号，越小越靠前")
	private Integer sortNo;

	@Schema(description = "备注")
	private String remark;
}
