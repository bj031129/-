package com.example.server.service;

import com.example.server.dto.UserPhoneMsg;
import com.example.server.entity.Contact;
import com.example.server.entity.ContactMsg;
import com.example.server.entity.Email;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 曾佳宝
 */
@Service
public interface UserService {

    Integer addContact(Contact contact);

    Integer deleteContact(Contact contact);

    List<ContactMsg> getContactList(String username);

    Integer updatePhone(UserPhoneMsg msg);

    List<Email> getMailsByReceiver(String username);

    List<Email> getMailsBySender(String username);
}
