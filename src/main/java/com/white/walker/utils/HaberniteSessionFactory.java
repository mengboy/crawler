package com.white.walker.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Created by admin on 2016/9/16.
 */
public class HaberniteSessionFactory {
    private static String CONFIG_FILE_LOCATION = "hibernate.cfg.xml";
    private static final ThreadLocal threadLocal = new ThreadLocal();
    private static Configuration configuration = new Configuration();
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private static String configFIle = CONFIG_FILE_LOCATION;

    private HaberniteSessionFactory() {}

    /**
     * 返回ThreadLocal Session instance
     * @return
     */
    public static Session getCurrentSession()
    {
        Session session = (Session) threadLocal.get();
        if(session == null || !session.isOpen() || session.isConnected())
        {
            if(sessionFactory == null)
            {
                rebbuildSessionFaction();
            }
            session = (sessionFactory != null) ? sessionFactory.openSession() : null;
            threadLocal.set(session);
        }
        return session;
    }

    /**
     * 创建sessionFactory
     */
    public static void rebbuildSessionFaction() {
        try {
            configuration = configuration.configure(configFIle);
            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }catch (Exception e)
        {
            System.err.println("Error Creating SessionFactory");
            e.printStackTrace();
        }
    }

    /**
     * 关闭session
     */
    public static void closeCurrentSession()
    {
        Session session = (Session) threadLocal.get();
        threadLocal.set(null);
        if(session != null)
        {
            session.close();
        }
    }

    /**
     * @return 返回sessionFactory
     */
    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * 设置配置文件路径
     * @param configFIle
     */
    public static void setConfigFIle(String configFIle)
    {
        HaberniteSessionFactory.configFIle = configFIle;
        sessionFactory = null;
    }

    /**
     * 获取configuration
     * @return
     */
    public static Configuration getConfiguration()
    {
        return configuration;
    }
}
