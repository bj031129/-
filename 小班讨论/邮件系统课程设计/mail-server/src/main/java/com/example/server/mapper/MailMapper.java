package com.example.server.mapper;

import com.example.server.entity.Email;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 曾佳宝
 */
@Repository
public interface MailMapper {

    /**
     * 添加邮件
     *
     * @param email 发送的邮件
     * @return 影响的行数
     * @throws Exception 数据库操作异常
     */
    Integer addMail(Email email) throws Exception;

    /**
     * 群发邮件
     *
     * @param emails 群发邮件
     * @return 插入的行数
     * @throws Exception 数据库操作异常
     */
    Integer addEmails(List<Email> emails) throws Exception;

    /**
     * 以username为接收方查询其收到的邮件列表，且邮件是未被标记为删除
     *
     * @param username 用户名
     * @return 返回该用户收到邮件列表
     */
    List<Email> findMailsByRcpt(String username);


    /**
     * 以username为发送方查询其收到的邮件列表，且邮件是未被标记为删除
     *
     * @param username 用户名
     * @return 返回该用户收到邮件列表
     */
    List<Email> findMailsBySender(String username);

    /**
     * 执行标记删除：根据用户标记要删除的邮件，让mid邮件的deleted=1
     *
     * @param mid
     */
    void updateDeletedMailByMid(Integer mid);

    /**
     * 执行持久的删除：删除邮件id是mid且deleted=1的邮件
     *
     * @param mid
     */
    void deleteMailByMid(Integer mid);

    /**
     * @param username 用户名
     * @return 已发送的邮件个数
     * @throws Exception 数据库操作异常
     */
    Integer getMailCount(String username) throws Exception;

}
