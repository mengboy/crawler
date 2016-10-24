package com.white.walker.service;

import com.white.walker.dao.Dao;
import com.white.walker.model.UserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 获取用户信息
 * Created by admin on 2016/9/16.
 */
public class GetUserInfo {
    private static String charset = "utf-8";
    private UserInfo userInfo = new UserInfo();
    private String strCookie;
    private Dao dao = new Dao();
    public void getUserInfo(Map<String, String> map){
        int i = 0;
        for(Map.Entry<String, String> entry : map.entrySet())
        {
            if(dao.SelectByUrl(entry.getValue()) == 0)
            {
                userInfo.setUsername(entry.getKey());
                userInfo.setUrl(entry.getValue());
                try {
                    String content = GetHtml.getHtml(entry.getValue(), strCookie);
                    getLocation(content);
                    getSex(content);
                    getDesc(content);
                    getImageUrl(content);
                    getImage(userInfo);
                    dao.save(userInfo);
                    userInfo.setEdu("");
                    userInfo.setJob("");
                } catch (ParserException e) {
                    throw new RuntimeException("获取用户主页失败");
                } catch (IOException e) {
                    throw new RuntimeException("获取用户主页失败");
                }
            }

            i++;
            System.out.println(i + ", size: " + map.size());
            System.out.println("url: " + entry.getValue());
        }
        try {
            String noTraceUrl = dao.getNoTrace();
            dao.saveHasGetFolowees("", noTraceUrl);
            getUserInfo(new FolloweesInfo().getMap(GetHtml.getHtml(noTraceUrl, strCookie)));
        } catch (ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("重新获取url失败");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("获取html页面失败");
        }
//        System.out.println("1234");
    }
    /**
     * 获取用户基本信息
     * @param content
     * @throws ParserException
     */
    public void getLocation(String content) throws ParserException {
        Parser parser = Parser.createParser(content, charset);
        TagNameFilter nameFilter = new TagNameFilter("span");
        NodeList list = parser.extractAllNodesThatMatch(nameFilter);

        for (int i = 0; i < list.size(); i++)
        {
            Tag node = (Tag) list.elementAt(i);
            String title = node.getAttribute("title");
            String classContent = node.getAttribute("class");
            if(title != null && (classContent.contains("item")))
            {
                if(classContent.contains("location"))
                {
                    userInfo.setAddress(title);
                }
                if(classContent.contains("business"))
                {
                    userInfo.setBusiness(title);
                }
                if(classContent.contains("employment") || classContent.contains("position"))
                {
                    userInfo.setJob(userInfo.getJob() + title);
                }
                if(classContent.contains("education"))
                {
                    userInfo.setEdu(userInfo.getEdu() + title);
                }
            }
        }
    }

    /**
     * 获取用户性别
     * @return
     */

    private void getSex(String content)
    {
        Parser parser = Parser.createParser(content, charset);
        TagNameFilter nameFilter = new TagNameFilter("i");
        NodeList list = null;
        try {
            list = parser.extractAllNodesThatMatch(nameFilter);
        } catch (ParserException e) {
            throw new RuntimeException("获取用户性别失败");
        }
        for (int i = 0; i < list.size(); i++)
        {
            Tag tag = (Tag) list.elementAt(i);
            String name = tag.getAttribute("class");
            if(name.contains("male"))
            {
                if(name.contains("female"))
                {
                    userInfo.setSex("女");
                }else {
                    userInfo.setSex("男");
                }
            }
        }
    }

    private void getDesc(String content)
    {
        Parser parser = Parser.createParser(content, charset);
        NodeFilter filter = new NodeClassFilter(ImageTag.class);
        TagNameFilter nameFilter = new TagNameFilter("textarea");
        NodeList list = null;
        try {
            list = parser.extractAllNodesThatMatch(nameFilter);
        } catch (ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户描述失败");
        }
        for (int i = 0; i < list.size(); i++)
        {
            Tag tag = (Tag) list.elementAt(i);
            String name = tag.getAttribute("name");
            if(name != null && name.trim().equals("description"))
            {
                userInfo.setInfo(tag.toPlainTextString().trim());
            }
        }
    }

    /**
     * 设置头像url
     * @return
     */
    private void getImageUrl(String content){
        Parser parser = Parser.createParser(content, charset);
        TagNameFilter nameFilter = new TagNameFilter("div");
        NodeList list = null;
        try {
            list = parser.extractAllNodesThatMatch(nameFilter);
        } catch (ParserException e) {
            e.printStackTrace();
            throw new RuntimeException("获取图片url失败");
        }
        for (int i = 0; i < list.size(); i++) {
            Tag tag = (Tag) list.elementAt(i);
            String aClass = tag.getAttribute("class");
            if (aClass != null && aClass.trim().equals("body clearfix")) {
                ImageTag node = (ImageTag) tag.getFirstChild().getNextSibling();
                userInfo.setImgurl(node.getAttribute("srcset").replaceAll("2x", "").trim());
            }
        }
    }

    /**
     * 获取图片
     * @return
     */
    private void getImage(UserInfo userInfo) throws IOException {
        File file = null;
        if(userInfo.getSex() != null)
        {
            if(userInfo.getSex().equals("女"))
            {
                file = new File("female/" + userInfo.getUsername() + ".jpg");
                if(!file.getParentFile().exists())
                {
                    file.getParentFile().mkdir();
                    file.createNewFile();
                }
            }
            if(userInfo.getSex().equals("男"))
            {
                file = new File("male/" + userInfo.getUsername() + ".jpg");
                if(!file.getParentFile().exists())
                {
                    file.getParentFile().mkdir();
                    file.createNewFile();
                }
            }
        }
        else {
            file = new File("unknown/" + userInfo.getUsername() + ".jpg");
            if(!file.getParentFile().exists())
            {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
        }

        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet httpGet = new HttpGet(userInfo.getImgurl());
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        if(entity != null && file != null)
        {
            byte[] bytes = new byte[1024];
            int num = 0;
            FileOutputStream os = new FileOutputStream(file);
            while ((num = inputStream.read(bytes)) != -1)
            {
                os.write(bytes, 0, num);
            }
            inputStream.close();
            os.close();
        }
    }
//    public LoginForm getLoginForm() {
//        return loginForm;
//    }
//
//    public void setLoginForm(LoginForm loginForm) {
//        this.loginForm = loginForm;
//    }

    public String getStrCookie() {
        return strCookie;
    }

    public void setStrCookie(String strCookie) {
        this.strCookie = strCookie;
    }
}
