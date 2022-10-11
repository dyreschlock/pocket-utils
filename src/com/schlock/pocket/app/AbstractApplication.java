package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

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
}
