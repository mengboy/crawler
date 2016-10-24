package com.white.walker.utils;

import com.white.walker.model.LoginForm;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 获取cookie
 * Created by admin on 2016/9/16.
 */
public class Cookie {
    private String strCookie;
    private String xsrf;
    private String url = "https://www.zhihu.com";
    private Map<String,String> cookieMap = new HashMap<String, String>(64);
    private CloseableHttpClient httpClient;
    private LoginForm loginForm;
    private String response;
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public void setLoginForm(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    private void getXsrf(){
        httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        try {
            //创建一个get请求用来接收_xsrf信息
            HttpGet get = new HttpGet(url);
            //获取_xsrf
            CloseableHttpResponse response = httpClient.execute(get);
            setCookie(response);
            String responseHtml = EntityUtils.toString(response.getEntity());
            xsrf = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getCookie()
    {
        try {
            //构造post数据
            List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("_xsrf", xsrf));
            valuePairs.add(new BasicNameValuePair("email", loginForm.getEmail()));
            valuePairs.add(new BasicNameValuePair("password", loginForm.getPassword()));
            valuePairs.add(new BasicNameValuePair("remember_me", loginForm.getRemember_me()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
            //创建一个post请求
            HttpPost post = new HttpPost("http://www.zhihu.com/login/email");
            //注入post数据
            post.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(post);
            //打印登录是否成功信息
            setResponse(httpResponse);
            //得到post请求返回的cookie信息
            strCookie = setCookie(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setResponse(HttpResponse httpResponse) throws ParseException, IOException {
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            response = responseString.replace("\r\n", "");
        }
    }

    /**
     * 获取cookie
     * @param httpResponse
     * @return
     */

    private String setCookie(HttpResponse httpResponse)
    {
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length==0)
        {
            System.out.println("----there are no cookies");
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if(i != headers.length-1)
            {
                cookie += ";";
            }
        }

        String cookies[] = cookie.split(";");
        for (String c : cookies)
        {
            c = c.trim();
            if(cookieMap.containsKey(c.split("=")[0]))
            {
                cookieMap.remove(c.split("=")[0]);
            }
            cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? "":(c.split("=").length ==2?c.split("=")[1]:c.split("=",2)[1]));
        }
        String cookiesTmp = "";
        for (String key :cookieMap.keySet())
        {
            cookiesTmp +=key+"="+cookieMap.get(key)+";";
        }

        return cookiesTmp.substring(0,cookiesTmp.length()-2);
    }

    public Cookie(LoginForm loginForm) {
        this.loginForm = loginForm;
        getXsrf();
        getCookie();
    }

    public String getStrCookie()
    {
        return strCookie;
    }
}
