package com.example.server.dto;

import java.util.List;

/**
 * @author 曾佳宝
 */
public class ForbiddenUser {

    private List<String> username;
    private Integer forbidden;

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public Integer getForbidden() {
        return forbidden;
    }

    public void setForbidden(Integer forbidden) {
        this.forbidden = forbidden;
    }

    @Override
    public String toString() {
        return "ForbiddenUser{" +
            "username=" + username +
            ", forbidden=" + forbidden +
            '}';
    }
}
