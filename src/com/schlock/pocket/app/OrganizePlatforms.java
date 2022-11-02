package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;
import com.schlock.pocket.services.database.PocketCoreDAO;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OrganizePlatforms extends AbstractDatabaseApplication
{
    protected OrganizePlatforms(String context)
    {
        super(context);
    }

    void process()
    {
        createListOfCores();

        updatePlatforms();
    }

    private void createListOfCores()
    {
        File assetDirectory = new File(config().getPocketAssetsDirectory());

        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                boolean isDirectory = file.isDirectory();

                return isDirectory;
            }
        };

        for(File directory : assetDirectory.listFiles(filter))
        {
            String namespace = directory.getName();
            createIfNew(namespace);
        }
    }

    private void createIfNew(String namespace)
    {
        PocketCore core = pocketCoreDAO().getByNamespace(namespace);
        if (core == null)
        {
            core = new PocketCore();
            core.setNamespace(namespace);

            session.save(core);

            System.out.println("New core " + namespace + " created in database.");
        }
    }

    private static final String JSON_FILE_EXT = ".json";

    private void updatePlatforms()
    {
        List<PocketCore> cores = pocketCoreDAO().getAllWithCompleteInformation();

        for(PocketCore core : cores)
        {
//            System.out.println("Complete core: " + core.getName());

            JSONObject json = PocketCore.createJSON(core);

            String filepath = config().getPocketPlatformsDirectory() + core.getNamespace() + JSON_FILE_EXT;

            deleteOldFile(filepath);
            writeToFile(filepath, json.toJSONString());
        }
    }

    private void deleteOldFile(String filepath)
    {
        File file = new File(filepath);
        if (file.exists())
        {
            file.delete();
        }
    }

    private void writeToFile(String filepath, String contents)
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

    private PocketCoreDAO pocketCoreDAO()
    {
        return new PocketCoreDAO(session);
    }

    public static void main(String args[]) throws Exception
    {
        new OrganizePlatforms(DeploymentConfiguration.LOCAL).run();
    }
}
