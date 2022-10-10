package com.schlock.pocket.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class DeploymentConfiguration
{
    public static final String LOCAL = "local";
    public static final String TEST = "test";

    private static final String README_FILE = "readme.md";

    private static final String CONFIG_PROPERTIES = "config.properties";

    private String context;

    private Properties properties;

    private DeploymentConfiguration()
    {
    }

    protected String getContext()
    {
        return context;
    }

    private void setContext(String context)
    {
        this.context = context;
    }

    private Properties getProperties()
    {
        if(properties == null)
        {
            try
            {
                loadProperties();
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    public void loadProperties() throws IOException
    {
        properties = new Properties();

        loadProperties(CONFIG_PROPERTIES);
    }

    private void loadProperties(String resource) throws IOException
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        if (stream != null)
        {
            try
            {
                properties.load(stream);
            }
            finally
            {
                stream.close();
            }
        }
        else
        {
            throw new FileNotFoundException("Property file missing: " + resource);
        }
    }


    public String getHibernateProperty(String property)
    {
        String hp = property + "." + getContext();
        return getProperties().getProperty(hp);
    }


    public static DeploymentConfiguration createDeploymentConfiguration(String context)
    {
        DeploymentConfiguration config = new DeploymentConfiguration();
        config.setContext(context);
        try
        {
            config.loadProperties();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        return config;
    }

    public String getReadmeFileContents()
    {
        Path readme = Path.of(README_FILE);
        try
        {
            return Files.readString(readme);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
