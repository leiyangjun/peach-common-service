package org.peach.common.service;

import org.peach.common.dto.ResetPwdDTO;
import org.peach.common.vo.UserVO;
import org.peach.common.mybatis.service.BaseInterfaceService;

/**
 * 用户管理：分页、详情、持久化（返回主键）、有效状态切换、重置口令。
 */
public interface UserService extends BaseInterfaceService<UserVO> {

	/**
	 * 根据主键是否有效判定新增或修改：{@code id == null} 或 {@code id <= 0} 为新增，否则为修改；返回主键。
 *
 * @author leiyangjun
 */
	Long persist(UserVO vo);

	/**
	 * 按主键切换有效状态：当前有效则逻辑删除（无效），当前无效则逻辑恢复（有效）；仅更新逻辑标记列。
	 *
	 * @return 切换后的 {@code valid}（0 或 1）
 *
 * @author leiyangjun
 */
	Short toggleValid(Long id);

	void resetPwd(ResetPwdDTO dto);

	/**
	 * 按主键物理删除用户：仅 {@code user_type = system} 允许；应用端用户拒绝。
	 *
	 * @param id 主键
 *
 * @author leiyangjun
 */
	void hardDelete(Long id);
}
