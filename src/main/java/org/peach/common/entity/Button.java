package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.SearchValue;
import org.peach.common.mybatis.annotation.TableName;

@Data
@TableName("cmn_button")
@Schema(description = "全局按钮字典：供菜单绑定选择；类型 add=新增 query=查询 update=修改 delete=删除 other=其他")
public class Button implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：短雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "按钮类型：add、query、update、delete、other")
    @SearchValue
    private String buttonType;

    @Schema(description = "按钮名称（展示）")
    @SearchValue
    private String buttonName;

    @Schema(description = "按钮编码（全局唯一，与前后端权限标识一致）")
    @SearchValue
    private String buttonCode;

    @Schema(description = "排序号，越小越靠前")
    private Integer sortNo;

    @Schema(description = "备注")
    private String remark;
}