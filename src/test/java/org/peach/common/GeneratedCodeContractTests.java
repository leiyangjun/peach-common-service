package org.peach.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;
import org.peach.common.entity.User;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mvc.web.BaseController;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.service.UserService;
import org.peach.common.service.impl.UserServiceImpl;
import org.peach.common.vo.UserVO;
import org.peach.common.web.UserController;

/**
 * 代码生成结果契约测试：校验 Service/Controller 的继承与泛型绑定，避免生成模板回归导致编译或注入不匹配。
 */
class GeneratedCodeContractTests {

	@Test
	void baseInterfaceService_declaresSharedCrudMethods() {
		assertThat(BaseInterfaceService.class.getMethods()).extracting("name")
				.contains("getById", "save", "listPage");
	}

	@Test
	void userService_extendsBaseInterfaceService() {
		assertThat(UserService.class.getInterfaces()).contains(BaseInterfaceService.class);
	}

	@Test
	void serviceImpl_shouldExtendBaseAbstractServiceAndImplementServiceInterface() {
		assertThat(BaseAbstractService.class.isAssignableFrom(UserServiceImpl.class)).isTrue();
		assertThat(UserService.class.isAssignableFrom(UserServiceImpl.class)).isTrue();

		Type superType = UserServiceImpl.class.getGenericSuperclass();
		assertThat(superType).isInstanceOf(ParameterizedType.class);
		ParameterizedType pt = (ParameterizedType) superType;
		assertThat(pt.getRawType()).isEqualTo(BaseAbstractService.class);
		assertThat(pt.getActualTypeArguments()).containsExactly(UserMapper.class, User.class, UserVO.class);
	}

	@Test
	void controller_shouldBindToVoAndServiceInterface() {
		assertThat(BaseController.class.isAssignableFrom(UserController.class)).isTrue();

		Type superType = UserController.class.getGenericSuperclass();
		assertThat(superType).isInstanceOf(ParameterizedType.class);
		ParameterizedType pt = (ParameterizedType) superType;
		assertThat(pt.getRawType()).isEqualTo(BaseController.class);
		assertThat(pt.getActualTypeArguments()).containsExactly(UserVO.class, UserServiceImpl.class);
	}
}
