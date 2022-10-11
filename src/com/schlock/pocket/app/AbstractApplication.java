package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

public abstract class AbstractApplication
{
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
