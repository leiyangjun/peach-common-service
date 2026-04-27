package org.peach.common.service;

import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.security.LoginUserVO;
import org.peach.common.vo.UserVO;

/**
 * 用户领域服务：通用 CRUD 继承 {@link BaseInterfaceService}{@code <UserVO>}，仅声明认证相关扩展。
 */
public interface UserService extends BaseInterfaceService<UserVO> {

	/**
	 * 当前登录用户：基于 {@link LoginUserVO} 合并访问令牌中的身份与库表最新展示字段（昵称、手机、头像等）。
	 */
	LoginUserVO currentProfile();
}
