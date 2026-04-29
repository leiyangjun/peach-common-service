package org.peach.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;

@Data
@TableName("cmn_menu_button")
@Schema(description = "菜单按钮表：按钮权限实体，角色授权建议绑定到按钮")
public class MenuButton implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "所属菜单 ID（cmn_menu.id）")
    private Long menuId;

    @Schema(description = "按钮编码：如 SYS_USER_ADD、SYS_USER_DELETE")
    private String buttonCode;

    @Schema(description = "按钮名称：如 新增、删除、导出")
    private String buttonName;

    @Schema(description = "权限点编码（可选）：如 system:user:add")
    private String permissionCode;

    @Schema(description = "同菜单下排序号，越小越靠前")
    private Integer orderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否有效：1=有效 0=无效（逻辑删除，SMALLINT）")
    @LogicDelete
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
