package com.white.walker.service;

import com.white.walker.model.LoginForm;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.IOException;

/**
 * 获取关注页面的url
 * Created by admin on 2016/9/16.
 */
public class GetHomeUser {
    private String title;
    private String aboutUrl;
    private String homePageUrl = "https://www.zhihu.com";
    private String followeesUrl;
    private void setTitleAndUrl(LoginForm loginForm, String cookie) throws IOException, ParserException {
        String homePage = GetHtml.getHtml(homePageUrl, cookie);
        Parser parser = Parser.createParser(homePage, "utf-8");
        NodeFilter filter = new NodeClassFilter(LinkTag.class);
        NodeList list = parser.extractAllNodesThatMatch(filter);
        for (int i = 0; i < list.size(); i++)
        {
            LinkTag  node = (LinkTag) list.elementAt(i);
            String aClass = node.getAttribute("class");
            String href = node.getAttribute("href");
            if (aClass != null)
            {
                aClass = aClass.trim();
                if (aClass.equals("zu-top-nav-userinfo"))
                {
                    title = node.toPlainTextString().trim();
                    aboutUrl = "https://www.zhihu.com" + href;
                    followeesUrl = aboutUrl + "/followees";
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAboutUrl() {
        return aboutUrl;
    }

    public void setAboutUrl(String aboutUrl) {
        this.aboutUrl = aboutUrl;
    }

    public String getFolloweesUrl(LoginForm loginForm, String cookie) throws IOException, ParserException {
        setTitleAndUrl(loginForm, cookie);
        return followeesUrl;
    }

    public void setFolloweesUrl(String followeesUrl) {
        this.followeesUrl = followeesUrl;
    }
}
