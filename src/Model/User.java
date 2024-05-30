package Model;

import java.io.Serializable;

public class User implements Serializable { // tạo constructor và getter, setter cho các thuộc tính của người dùng
    private String id;
    private String nameLogin;
    private String name;
    private String password;
    private Boolean isConnected;
    private Boolean hasNewMessage = false;

    public User(String id, String nameLogin, String name, String password) {
        this.id = id;
        this.nameLogin = nameLogin;
        this.name = name;
        this.password = password;
    }

    public User( String nameLogin, String name, String password) {
        this.nameLogin = nameLogin;
        this.name = name;
        this.password = password;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameLogin() {
        return nameLogin;
    }

    public void setNameLogin(String nameLogin) {
        this.nameLogin = nameLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public Boolean getHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(Boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    @Override
    public String toString() {
        String s = this.name + "-" + this.isConnected;
        return s;
    }

    public String getname() {
        return this.name;
    }

    public String getUsername() {
        return this.nameLogin;

    }
}
