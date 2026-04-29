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
@TableName("cmn_role_menu")
@Schema(description = "角色菜单关联表：一条记录代表一个角色拥有一个菜单权限")
public class RoleMenu implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键：雪花 64 位，对应 Java long")
    @ID
    private Long id;

    @Schema(description = "关系编码：如 RM_ROLE_ADMIN_SYS_USER")
    private String relationCode;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "菜单 ID")
    private Long menuId;

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
