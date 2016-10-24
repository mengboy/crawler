package com.white.walker.utils;

import com.white.walker.model.LoginForm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取登录信息
 * Created by admin on 2016/9/20.
 */
public class LoginInfo {
    private static LoginForm loginForm = new LoginForm();
    public static LoginForm getLoginForm() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = LoginInfo.class.getClassLoader().getResourceAsStream("login.properties");
        properties.load(inputStream);
        inputStream.close();
        loginForm.setEmail(properties.getProperty("email"));
        loginForm.setPassword(properties.getProperty("password"));
        return loginForm;
    }
}
