package com.example.server.dto;

/**
 * @author 曾佳宝
 */
public class ServerPortMsg {

    private Integer sid;
    private Integer serverType;
    private String serverPort;

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getServerType() {
        return serverType;
    }

    public void setServerType(Integer serverType) {
        this.serverType = serverType;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
