package com.example.server.mapper;

import com.example.server.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author 曾佳宝
 */
@Repository
public interface LoginMapper {

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户数据
     * @throws Exception 数据库操作异常
     */
    User findUserByUserName(String userName) throws Exception;

    /**
     * 修改密码
     *
     * @param userName    用户们
     * @param newPassword 新密码
     * @return 影响的行数
     * @throws Exception 数据库操作异常
     */
    Integer updatePassword(String userName, String newPassword) throws Exception;

    /**
     * 插入新用户
     *
     * @param user 新注册的用户
     * @return 影响的行数
     * @throws Exception 数据库操作异常
     */
    Integer insertNewUser(User user) throws Exception;
}
