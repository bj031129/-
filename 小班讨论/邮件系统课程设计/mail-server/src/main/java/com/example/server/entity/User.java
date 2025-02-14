package com.example.server.entity;

import io.swagger.models.auth.In;
import java.sql.Timestamp;

/**
 * @author 曾佳宝
 */
public class User {

    private String username;
    private String password;
    private String phone;
    private Integer accountType;
    private Timestamp latestLoginTime;
    private String latestLoginIp;
    private Integer mailBoxSize;
    private String avatarUrl;
    private Integer logout;
    private Integer forbidden;

    public Integer getLogout() {
        return logout;
    }

    public void setLogout(Integer logout) {
        this.logout = logout;
    }

    public User() {

    }

    public User username(String username) {
        this.setUsername(username);
        return this;
    }

    public User password(String password) {
        this.setPassword(password);
        return this;
    }

    public User phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public User accountType(Integer accountType) {
        this.setAccountType(accountType);
        return this;
    }

    public User latestLoginTime(Timestamp latestLoginTime) {
        this.setLatestLoginTime(latestLoginTime);
        return this;
    }

    public User latestLoginIp(String latestLoginIp) {
        this.setLatestLoginIp(latestLoginIp);
        return this;
    }

    public User mailBoxSize(Integer mailBoxSize) {
        this.setMailBoxSize(mailBoxSize);
        return this;
    }

    public User avatarURL(String avatarUrl) {
        this.setAvatarUrl(avatarUrl);
        return this;
    }

    public User isLogout(Integer logout) {
        this.setLogout(logout);
        return this;
    }

    public User isForbidden(Integer forbidden) {
        this.setForbidden(forbidden);
        return this;
    }

    public User build() {
        return this;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public Timestamp getLatestLoginTime() {
        return latestLoginTime;
    }

    public void setLatestLoginTime(Timestamp latestLoginTime) {
        this.latestLoginTime = latestLoginTime;
    }

    public String getLatestLoginIp() {
        return latestLoginIp;
    }

    public void setLatestLoginIp(String latestLoginIp) {
        this.latestLoginIp = latestLoginIp;
    }

    public Integer getMailBoxSize() {
        return mailBoxSize;
    }

    public void setMailBoxSize(Integer mailBoxSize) {
        this.mailBoxSize = mailBoxSize;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getForbidden() {
        return forbidden;
    }

    public void setForbidden(Integer forbidden) {
        this.forbidden = forbidden;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", accountType=" + accountType +
                ", latestLoginTime=" + latestLoginTime +
                ", latestLoginIp='" + latestLoginIp + '\'' +
                ", mailBoxSize=" + mailBoxSize +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", logout=" + logout +
                '}';
    }
}
