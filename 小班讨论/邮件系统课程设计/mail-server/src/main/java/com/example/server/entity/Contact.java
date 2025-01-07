package com.example.server.entity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author 曾佳宝
 */
public class Contact {

    private String contactName;
    private String username;
    private Timestamp addTime;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }
}
