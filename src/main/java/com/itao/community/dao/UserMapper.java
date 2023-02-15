package com.itao.community.dao;

import com.itao.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shkstart
 * @create 2023--14 16:06
 */
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
