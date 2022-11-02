package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.services.DeploymentConfiguration;
import com.schlock.pocket.services.database.PocketCoreDAO;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
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
            String filepath = config().getPocketPlatformsDirectory() + core.getNamespace() + JSON_FILE_EXT;
            String json = generateJSONforCore(core);

            deleteOldFile(filepath);
            writeToFile(filepath, json);
        }
    }

    private static final String PLATFORM = "platform";

    private String generateJSONforCore(PocketCore core)
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PocketCoreCategory.class, new JsonSerializer<PocketCoreCategory>()
                {
                    @Override
                    public JsonElement serialize(PocketCoreCategory src, Type typeOfSrc, JsonSerializationContext context)
                    {
                        //This will output "category": "some category" rather than the entire category object.
                        String categoryName = src.getName();

                        return new JsonPrimitive(categoryName);
                    }
                })
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        //JSON is generated automatically from @Expose tags on PocketCore class

        //Pocket JSON requires that the core be wrapped in the "platform" tag
        JsonObject base = new JsonObject();
        JsonElement tree = gson.toJsonTree(core);

        base.add(PLATFORM, tree);

        return gson.toJson(base);
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
