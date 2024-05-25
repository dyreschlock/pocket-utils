package com.schlock.pocket.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DeploymentConfiguration
{
    private static final String POCKET_DIRECTORY = "pocket.directory";
    private static final String POCKET_UTILITY_DIRECTORY = "utility.directory";

    private static final String PLAYSTATION_DIRECTORY = "playstation.directory";

    private static final String BOXART_SOURCE_URL = "boxart.source.url";
    private static final String BOXART_STORAGE_FOLDER = "library_images/boxart/";
    private static final String BOXART_CONVERTED_FOLDER = "library_images/boxart_converted/";

    private static final String ASSETS_FOLDER = "Assets/";
    private static final String LIBRARY_IMAGES_FOLDER = "System/Library/Images/";
    private static final String PLATFORMS_FOLDER = "Platforms/";

    private static final String ROMZIP_SOURCE_URL = "romzip.source.url";
    private static final String ROMZIP_HBSOURCE_URL = "romzip.hbsource.url";
    private static final String ROMZIP_STORAGE_FOLDER = "arcade_roms/";

    private static final String MRA_TODO_FOLDER = "mra_to_process/";

    private static final String PLATFORM_IMAGES_FOLDER = "platform_images/";

    private static final String WEBSITE_IMAGE_DIRECTORY = "website.image.directory";
    private static final String WEBSITE_DATA_DIRECTORY = "website.data.directory";

    private static final String DATA_DIRECTORY = "data/";

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

    public String getPocketUtilityDirectory()
    {
        return getProperties().getProperty(POCKET_UTILITY_DIRECTORY);
    }

    public String getPlaystationDirectory()
    {
        return getProperties().getProperty(PLAYSTATION_DIRECTORY);
    }

    public String getBoxartSourceUrl()
    {
        return getProperties().getProperty(BOXART_SOURCE_URL);
    }

    public String getBoxartStorageDirectory()
    {
        return getPocketUtilityDirectory() + BOXART_STORAGE_FOLDER;
    }

    public String getBoxartThumbnailProcessingDirectory()
    {
        return getPocketUtilityDirectory() + BOXART_CONVERTED_FOLDER;
    }

    public String getPocketAssetsDirectory()
    {
        return getProperties().getProperty(POCKET_DIRECTORY) + ASSETS_FOLDER;
    }

    public String getPocketPlatformsDirectory()
    {
        return getProperties().getProperty(POCKET_DIRECTORY) + PLATFORMS_FOLDER;
    }

    public String getPocketLibraryDirectory()
    {
        return getProperties().getProperty(POCKET_DIRECTORY) + LIBRARY_IMAGES_FOLDER;
    }

    public String getDataDirectory()
    {
        return DATA_DIRECTORY;
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
        return getPocketUtilityDirectory() + ROMZIP_STORAGE_FOLDER;
    }

    public String getMRAToBeProcessedDirectory()
    {
        return getPocketUtilityDirectory() + MRA_TODO_FOLDER;
    }

    public String getPlatformImagesDirectory()
    {
        return getPocketUtilityDirectory() + PLATFORM_IMAGES_FOLDER;
    }

    public String getWebsiteImageDirectory()
    {
        return getProperties().getProperty(WEBSITE_IMAGE_DIRECTORY);
    }

    public String getWebsiteDataDirectory()
    {
        return getProperties().getProperty(WEBSITE_DATA_DIRECTORY);
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
