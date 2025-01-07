package com.example.server.dto;

/**
 * @author 曾佳宝
 */
public class UserNameAndType {

    private String username;
    private Integer accountType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
}
