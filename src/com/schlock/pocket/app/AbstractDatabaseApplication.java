package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public abstract class AbstractDatabaseApplication extends AbstractApplication
{
    public final static String HIBERNATE_USERNAME = "hibernate.connection.username";
    public final static String HIBERNATE_PASSWORD = "hibernate.connection.password";
    public final static String HIBERNATE_URL = "hibernate.connection.url";

    public final static String HIBERNATE_HIKARI_USERNAME = "hibernate.hikari.dataSource.user";
    public final static String HIBERNATE_HIKARI_PASSWORD = "hibernate.hikari.dataSource.password";
    public final static String HIBERNATE_HIKARI_URL = "hibernate.hikari.dataSource.url";

    protected SessionFactory sessionFactory;
    protected Session session;

    protected AbstractDatabaseApplication(String context)
    {
        super(context);
    }

    abstract void process();

    public void run()
    {
        try
        {
            setupDatabase();

            process();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            teardownDatabase();
        }
    }

    protected Session getSession()
    {
        return session;
    }

    private void setupDatabase()
    {
        System.setProperty("log4j.debug", "false");

        final String username = config().getHibernateProperty(HIBERNATE_USERNAME);
        final String password = config().getHibernateProperty(HIBERNATE_PASSWORD);
        final String url = config().getHibernateProperty(HIBERNATE_URL);


        Configuration dbconfig = new Configuration();

        dbconfig.configure(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);

        dbconfig.setProperty(HIBERNATE_USERNAME, username);
        dbconfig.setProperty(HIBERNATE_PASSWORD, password);
        dbconfig.setProperty(HIBERNATE_URL, url);

        dbconfig.setProperty(HIBERNATE_HIKARI_USERNAME, username);
        dbconfig.setProperty(HIBERNATE_HIKARI_PASSWORD, password);
        dbconfig.setProperty(HIBERNATE_HIKARI_URL, url);

        sessionFactory = dbconfig.buildSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    private void teardownDatabase()
    {
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }
}
