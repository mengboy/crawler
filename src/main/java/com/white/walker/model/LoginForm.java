package com.white.walker.model;

/**
 * Created by admin on 2016/9/16.
 */
public class LoginForm {
    private String email;
    private String password;
    private String remember_me = "true";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemember_me() {
        return remember_me;
    }

    public void setRemember_me(String remember_me) {
        this.remember_me = remember_me;
    }
}
