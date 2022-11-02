package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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

    protected void executeScript(String scriptName)
    {
        String shellFilepath = System.getProperty("user.dir") + "/src";
        String shellCommand = String.format("zsh %s", scriptName);

        try
        {
            Process p = Runtime.getRuntime().exec(shellCommand, null, new File(shellFilepath));
            p.waitFor();

            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while(output.ready())
            {
                System.out.println(output.readLine());
            }

            BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while(errors.ready())
            {
                System.out.println(errors.readLine());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
