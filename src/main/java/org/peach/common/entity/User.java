package org.peach.common.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.LogicDelete;
import org.peach.common.mybatis.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;

@TableName("cmn_user")
@Schema(description = "用户：系统侧与 C 端共用一张表，由 subjectType 区分")
public class User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	@ID
	private Long id;

	@Schema(description = "主体：INTERNAL=系统侧，CUSTOMER=C 端")
	private String subjectType;

	@Schema(description = "登录名；系统侧必填，C 端可空")
	private String username;

	@Schema(description = "密码（生产请存摘要）；免密登录可空")
	private String password;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "真实姓名或对内展示名")
	private String realName;

	@Schema(description = "手机号")
	private String mobile;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "头像地址")
	private String avatarUrl;

	@Schema(description = "性别：0 未知，1 男，2 女")
	private Integer gender;

	@Schema(description = "首次注册终端：ADMIN_WEB、MINI_APP、APP、OPEN_API 等")
	private String registerClient;

	@Schema(description = "最近登录时间")
	private Date lastLoginTime;

	@Schema(description = "最近登录终端")
	private String lastLoginClient;

	@Schema(description = "开放体系用户标识预留（OAuth subject 等）")
	private String openUnionId;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "是否有效：1=有效 0=无效")
	@LogicDelete
	private String valid;

	@Schema(description = "创建时间；插入时由 DEFAULT 填充，也可在应用内显式赋值")
	private Date createTime;

	@Schema(description = "最后更新时间；插入时由 DEFAULT 填充，更新由应用程序赋值")
	private Date editTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getRegisterClient() {
		return registerClient;
	}

	public void setRegisterClient(String registerClient) {
		this.registerClient = registerClient;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginClient() {
		return lastLoginClient;
	}

	public void setLastLoginClient(String lastLoginClient) {
		this.lastLoginClient = lastLoginClient;
	}

	public String getOpenUnionId() {
		return openUnionId;
	}

	public void setOpenUnionId(String openUnionId) {
		this.openUnionId = openUnionId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEditTime() {
		return editTime;
	}

	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}
}
