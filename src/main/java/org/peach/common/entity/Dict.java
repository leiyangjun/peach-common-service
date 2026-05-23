package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.SearchValue;
import org.peach.common.mybatis.annotation.TableName;

/**
 * 码表（字典项）实体：同一 {@code dictType} 下 {@code dictValue} 全局唯一；{@code status} 控制启用/停用（无逻辑删除列）。
 *
 * @author leiyangjun
 */
@Data
@TableName("cmn_dict")
@Schema(description = "码表项：类型 + 存储值唯一；status 控制启用")
public class Dict implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位")
	@ID
	private Long id;

	@Schema(description = "字典类型/分组编码，如 user_status")
	@SearchValue
	private String dictType;

	@Schema(description = "展示标签")
	@SearchValue
	private String dictLabel;

	@Schema(description = "存储值，与 dictType 组合唯一")
	@SearchValue
	private String dictValue;

	@Schema(description = "同类型下排序号，越小越靠前")
	private Integer sortNo;

	@Schema(description = "状态：1=启用 0=停用")
	@LogicDelete()
	private Short status;

	@Schema(description = "备注")
	@SearchValue
	private String remark;

	@Schema(description = "父级主键，0 表示根节点")
	private Long parentId;

	@Schema(description = "前端附加 CSS 类名")
	private String cssClass;

	@Schema(description = "列表/标签展示样式类")
	private String listClass;

	@Schema(description = "是否默认项：1=是 0=否")
	private Short isDefault;

	@Schema(description = "创建人 ID")
	private Long creator;

	@Schema(description = "修改人 ID")
	private Long editor;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}
