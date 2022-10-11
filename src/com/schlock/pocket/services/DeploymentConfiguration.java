package com.schlock.pocket.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DeploymentConfiguration
{
    private static final String BOXART_SOURCE_URL = "boxart.source.url";
    private static final String BOXART_STORAGE_DIRECTORY = "boxart.storage.directory";

    private static final String POCKET_ASSETS_DIRECTORY = "pocket.assets.directory";
    private static final String POCKET_LIBRARY_DIRECTORY = "pocket.library.directory";

    private static final String ROMZIP_SOURCE_URL = "romzip.source.url";
    private static final String ROMZIP_HBSOURCE_URL = "romzip.hbsource.url";
    private static final String ROMZIP_STORAGE_DIRECTORY = "romzip.storage.directory";

    private static final String PREPARATION_MRA_DIRECTORY = "preparation.mra.directory";
    private static final String PREPARATION_LIBRARY_DIRECTORY = "preparation.library.directory";

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

    public String getBoxartSourceUrl()
    {
        return getProperties().getProperty(BOXART_SOURCE_URL);
    }

    public String getBoxartStorageDirectory()
    {
        return getProperties().getProperty(BOXART_STORAGE_DIRECTORY);
    }

    public String getPocketAssetsDirectory()
    {
        return getProperties().getProperty(POCKET_ASSETS_DIRECTORY);
    }

    public String getPocketLibraryDirectory()
    {
        return getProperties().getProperty(POCKET_LIBRARY_DIRECTORY);
    }

    public String getRomzipSourceUrl()
    {
        //"https://archive.org/download/jogos_arcade"
        //"https://archive.org/download/mame-merged/mame-merged/";
        return getProperties().getProperty(ROMZIP_SOURCE_URL);
    }

    public String getRomzipHBSourceUrl()
    {
        return getProperties().getProperty(ROMZIP_HBSOURCE_URL);
    }

    public String getRomzipStorageDirectory()
    {
        return getProperties().getProperty(ROMZIP_STORAGE_DIRECTORY);
    }

    public String getPreparationMraDirectory()
    {
        return getProperties().getProperty(PREPARATION_MRA_DIRECTORY);
    }

    public String getPreparationLibraryDirectory()
    {
        return getProperties().getProperty(PREPARATION_LIBRARY_DIRECTORY);
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
