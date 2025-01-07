package com.example.server.dto;

/**
 * @author 曾佳宝
 */
public class NewUserMessage {

    private String username;
    private String password;
    private Integer accountType;
    private Integer mailBoxSize;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public Integer getMailBoxSize() {
        return mailBoxSize;
    }

    public void setMailBoxSize(Integer mailBoxSize) {
        this.mailBoxSize = mailBoxSize;
    }
}
