package Model;

import java.io.Serializable;

public class Client implements Serializable {

    private String ipAddress;
    private Integer port;
    private String username;
    private String userId;

    public Client(String ipAddress, Integer port, String username, String userId) { // tạo constructor và getter, setter cho các thuộc tính của người dùng
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
