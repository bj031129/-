package com.example.server.dto;

/**
 * @author 曾佳宝
 */
public class ServerState {

    private Integer serverName;
    private Boolean state;

    public Integer getServerName() {
        return serverName;
    }

    public void setServerName(Integer serverName) {
        this.serverName = serverName;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
