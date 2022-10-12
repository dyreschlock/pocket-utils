package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;

public abstract class AbstractApplication
{
    public static final String COMMON = "common";
    public static final String COMMON_FOLDER = COMMON + "/";

    private DeploymentConfiguration config;

    protected AbstractApplication(String context)
    {
        this.config = DeploymentConfiguration.createDeploymentConfiguration(context);
    }

    protected DeploymentConfiguration config()
    {
        return config;
    }

    protected void createDirectories(String... locations)
    {
        for(String location : locations)
        {
            File folder = new File(location);
            if (!folder.exists())
            {
                folder.mkdirs();
            }
        }
    }
}
