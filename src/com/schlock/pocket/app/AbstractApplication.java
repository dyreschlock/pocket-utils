package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.*;
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

    protected List<String> getRomLocationAbsolutePath(PocketCore core)
    {
        if (core.getPlatformId().equals("vectrex"))
        {
            String temp = "";
        }

        String coreDirectory = config().getPocketAssetsDirectory() + core.getPlatformId() + "/";

        List<String> paths = new ArrayList<>();
        if (core.getExecutionDirectories().isEmpty())
        {
            paths.add(coreDirectory + COMMON_FOLDER);
        }
        else
        {
            for(String dir : core.getExecutionDirectories())
            {
                paths.add(coreDirectory + dir + "/");
            }
        }
        return paths;
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

    protected void deleteOldFile(String filepath)
    {
        File file = new File(filepath);
        if (file.exists())
        {
            file.delete();
        }
    }

    protected void writeStringToFile(String filepath, String contents)
    {
        File file = new File(filepath);

        try
        {
            FileWriter writer = new FileWriter(file, false);
            writer.write(contents);
            writer.close();

            System.out.println("New core file written: " + filepath);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
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
