package com.schlock.pocket;

import com.schlock.bot.services.DeploymentConfiguration;
import com.schlock.bot.services.database.DatabaseModule;
import com.schlock.bot.services.impl.DeploymentConfigurationImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public abstract class AbstractStandalongDatabaseApp
{
    protected DeploymentConfiguration config;

    protected SessionFactory sessionFactory;
    protected Session session;

    abstract void process();

    public void run()
    {
        try
        {
            setupDatabase();

            process();
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
        config = DeploymentConfigurationImpl.createDeploymentConfiguration(DeploymentConfiguration.LOCAL);

        final String username = config.getHibernateProperty(DatabaseModule.HIBERNATE_USERNAME);
        final String password = config.getHibernateProperty(DatabaseModule.HIBERNATE_PASSWORD);
        final String url = config.getHibernateProperty(DatabaseModule.HIBERNATE_URL);


        Configuration dbconfig = new Configuration();

        dbconfig.configure(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);

        dbconfig.setProperty(DatabaseModule.HIBERNATE_USERNAME, username);
        dbconfig.setProperty(DatabaseModule.HIBERNATE_PASSWORD, password);
        dbconfig.setProperty(DatabaseModule.HIBERNATE_URL, url);

        dbconfig.setProperty(DatabaseModule.HIBERNATE_HIKARI_USERNAME, username);
        dbconfig.setProperty(DatabaseModule.HIBERNATE_HIKARI_PASSWORD, password);
        dbconfig.setProperty(DatabaseModule.HIBERNATE_HIKARI_URL, url);

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
