package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApplication
{
    protected static final String OVERWRITE_PLATFORM_IMAGES_SCRIPT = "overwrite_platform_images.sh";

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

    protected String getRomLocationAbsolutePath(PocketCore core)
    {
        String coreDirectory = config().getPocketAssetsDirectory() + core.getNamespace() + "/";
        if (core.getExecutionDirectory() != null)
        {
            return coreDirectory + core.getExecutionDirectory() + "/";
        }
        return coreDirectory + COMMON_FOLDER;
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

    protected void executeShellScript(String scriptName)
    {
        String[] commands = new String[2];
        commands[0] = "zsh";
        commands[1] = scriptName;

        executeShellCommand(commands);
    }

    protected List<String> executeShellCommand(String[] commandAndArgs)
    {
        List<String> outputContents = new ArrayList<>();

        String shellFilepath = System.getProperty("user.dir") + "/src";
        try
        {
            Process p = Runtime.getRuntime().exec(commandAndArgs, null, new File(shellFilepath));
            p.waitFor();

            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while(output.ready())
            {
                String line = output.readLine();

                outputContents.add(line);
                System.out.println(line);
            }

            BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while(errors.ready())
            {
                String line = errors.readLine();

                outputContents.add(line);
                System.out.println(line);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return outputContents;
    }
}
