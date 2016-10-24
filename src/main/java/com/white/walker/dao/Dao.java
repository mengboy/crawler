package com.white.walker.dao;

import com.white.walker.model.UserInfo;
import com.white.walker.model.UserUrl;
import com.white.walker.utils.HaberniteSessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Created by admin on 2016/9/17.
 */
public class Dao {
    /**
     * 保存
     * @param userInfo
     */
    public void save(UserInfo userInfo)
    {
        Session session = HaberniteSessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(userInfo);
        transaction.commit();
        session.close();
    }

    /**
     * 根据url查询是否已经获取过
     * @param url
     */
    public int SelectByUrl(String url)
    {
        String hql = "select count(*) from UserInfo as u where u.url = '"+ url +"'";
        Session session = HaberniteSessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        List result = query.list();
        Number number = (Number) result.get(0);
        session.close();
        return number.intValue();
    }

    /**
     * 获取易查询的url
     * @return
     */
    public List getUrl()
    {
        String hql = "select url from UserInfo";
        Session session = HaberniteSessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        List result = query.list();
        session.close();
        return result;
    }

    /**
     * 保存已获取关注的人的url
     * @param title
     * @param url
     */
    public void saveHasGetFolowees(String title, String url)
    {
        UserUrl userUrl = new UserUrl();
        userUrl.setTitle(title);
        userUrl.setUserurl(url);
        Session session = HaberniteSessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(userUrl);
        transaction.commit();
        session.close();
    }

    /**
     * 根据url查询是否已经获取该对象关注的用户
     * @param url
     * @return
     */
    public int selectHasGet(String url)
    {
        String hql = "select count(*) from UserUrl as u where u.userurl = '"+ url +"'";
        Session session = HaberniteSessionFactory.getCurrentSession();
        Query query = session.createQuery(hql);
        List result = query.list();
        Number number = (Number) result.get(0);
        session.close();
        return number.intValue();
    }

    /**
     * 获取一个没有遍历关注对象的url
     */

    public String getNoTrace()
    {
        List list = getUrl();
        for(int i = list.size() - 1; i >= 0; i--)
        {
            System.out.println("i: " + i);
            String url = (String) list.get(i);
            if(selectHasGet(url) == 0)
            {
                System.out.println("dao Url: " + url);
                return url;
            }
        }
//        System.out.println("dao NUll");
        return null;
    }
}
