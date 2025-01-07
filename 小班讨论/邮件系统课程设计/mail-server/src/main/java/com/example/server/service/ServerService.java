package com.example.server.service;

import com.example.server.entity.Email;
import com.example.server.entity.Log;
import com.example.server.entity.User;
import java.util.List;

/**
 * @author 曾佳宝
 */
public interface ServerService {

  /**
   * 判断服务器名后缀是否正确
   * @param userName
   * @return
   */
  Boolean isRightServer(String userName);

  /**
   * 获得服务器名
   * @return
   */
  String getServerName();

  /**
   * 为邮件加上服务器名
   * @param emails
   * @return
   */
  List<Email> addServerNameForMail(List<Email> emails);

  /**
   * 为用户加上服务器名
   * @param users
   * @return
   */
  List<User> addServerNameForUser(List<User> users);

  /**
   * 为日志操作者加上服务器名
   * @param logs
   * @return
   */
  List<Log> addServerNameForLogs(List<Log> logs);
}
