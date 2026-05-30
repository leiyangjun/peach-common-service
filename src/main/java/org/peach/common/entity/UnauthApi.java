package org.peach.common.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.SearchValue;
import org.peach.common.mybatis.annotation.TableName;
import org.peach.common.mybatis.annotation.Unique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("cmn_unauth_api")
@Schema(description = "网关 JWT 免鉴权 API")
public class UnauthApi implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@ID
	private Long id;

	private String method;

	@SearchValue
	private String summary;

	@SearchValue
	private String urlPath;

	@SearchValue
	private String serviceName;

	private Short isExternal;

	/** 访问类型：1=免登录白名单 2=需登录免权限校验 */
	private Short accessType;

	@Unique
	@SearchValue
	private String finalPath;

	private Short deletable;

	private Short valid;

	private Long creator;

	private Long editor;

	private Date createTime;

	private Date editTime;
}
