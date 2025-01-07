package com.example.server.dto;

import com.example.server.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 曾佳宝
 */
public class MassEmail {

    private String senderEmail;
    private List<String> receiverEmails;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp sendTime;
    private String subject;
    private String body;
    private String avatarUrl;

    public MassEmail(String senderEmail, String receiverEmail, String subject, String body) {
        this.senderEmail = senderEmail;
        this.receiverEmails = new ArrayList<>();
        this.receiverEmails.add(receiverEmail);
        this.subject = subject;
        this.sendTime = DateUtil.getCurrentTime();
        this.body = body;
    }

    public MassEmail(){

    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public List<String> getReceiverEmails() {
        return receiverEmails;
    }

    public void setReceiverEmails(List<String> receiverEmails) {
        this.receiverEmails = receiverEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }
}
