package com.example.server.service.impl;

import com.example.server.config.SpringContextConfig;
import com.example.server.entity.Email;
import com.example.server.entity.Log;
import com.example.server.entity.User;
import com.example.server.mapper.AdminMapper;
import com.example.server.service.ServerService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author 曾佳宝
 */
@Service("ServerServiceImpl")
public class ServerServiceImpl implements ServerService {
  private final AdminMapper adminMapper = SpringContextConfig.getBean(AdminMapper.class);

  @Override
  public Boolean isRightServer(String userName) {
    try {
      String serverName = adminMapper.selectServerMessage().get(0).getServerName();
      String[] mailAddress = userName.split("@");
      if (mailAddress[1].equals(serverName)) {
        return true;
      }
      else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public String getServerName() {
    try {
      String serverName = adminMapper.selectServerMessage().get(0).getServerName();
      return "@" + serverName;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Email> addServerNameForMail(List<Email> emails) {
    for (int i = 0; i< emails.size(); i++) {
      String sender = emails.get(i).getSenderEmail() + getServerName();
      emails.get(i).setSenderEmail(sender);
      String receiver = emails.get(i).getReceiverEmail() + getServerName();
      emails.get(i).setReceiverEmail(receiver);
    }
    return emails;
  }

  @Override
  public List<User> addServerNameForUser(List<User> users) {
    for (int i = 0; i< users.size(); i++) {
      String sender = users.get(i).getUsername() + getServerName();
      users.get(i).setUsername(sender);
    }
    return users;
  }

  @Override
  public List<Log> addServerNameForLogs(List<Log> logs) {
    for (int i = 0; i< logs.size(); i++) {
      String sender = logs.get(i).getUsername() + getServerName();
      logs.get(i).setUsername(sender);
    }
    return logs;
  }
}
