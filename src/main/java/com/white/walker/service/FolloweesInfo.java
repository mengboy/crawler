package com.white.walker.service;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取关注者title、url
 * Created by admin on 2016/9/16.
 */
public class FolloweesInfo {
    private Map<String, String> map = new HashMap<String, String>();
    public Map<String, String> getMap(String content) throws ParserException {
        Parser parser = Parser.createParser(content, "utf-8");
        NodeFilter filter = new NodeClassFilter(LinkTag.class);
        NodeList list = parser.extractAllNodesThatMatch(filter);
        for (int i = 0; i < list.size(); i++)
        {
            Node node = list.elementAt(i);
            LinkTag tag = (LinkTag) list.elementAt(i);
            String href = tag.getAttribute("href");
            String title = tag.getAttribute("title");
            String contain = "https://www.zhihu.com/people";
            if (href.contains(contain) && title != null)
            {
                 map.put(title, href+"/followees");
            }
        }
        return map;
    }
}
