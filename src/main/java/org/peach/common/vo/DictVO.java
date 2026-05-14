package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 码表管理对外 VO：与 {@link org.peach.common.entity.Dict} 行字段一致，用于详情、列表行与保存请求体（不含分页专用参数）。
 */
@Data
public class DictVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "字典类型/分组编码")
	private String dictType;

	@Schema(description = "展示标签")
	private String dictLabel;

	@Schema(description = "存储值")
	private String dictValue;

	@Schema(description = "排序号")
	private Integer sortNo;

	@Schema(description = "状态：1 启用 0 停用")
	private Short status;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "父级主键，0 为根")
	private Long parentId;

	@Schema(description = "附加 CSS 类名")
	private String cssClass;

	@Schema(description = "列表/标签样式类")
	private String listClass;

	@Schema(description = "是否默认：1 是 0 否")
	private Short isDefault;

	@Schema(description = "创建人")
	private Long creator;

	@Schema(description = "修改人")
	private Long editor;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}
