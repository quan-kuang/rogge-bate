package com.loyer.modules.system.mapper.postgresql;

import com.loyer.common.core.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author kuangq
 * @date 2020-05-13 10:05
 */
public interface UserMapper {

    User loadUserByUsername(@Param("type") String type, @Param("username") String username);

    Integer saveUser(User user);

    List<User> selectUser(User user);

    Integer deleteUser(String... uuids);

    Integer updateUser(User user);

    Integer saveUserLink(User user);

    List<String> selectUserLink(String userId);

    Integer deleteUserLink(String userId);
}