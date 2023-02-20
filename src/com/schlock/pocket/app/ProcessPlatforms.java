package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class ProcessPlatforms extends AbstractDatabaseApplication
{
    private static final boolean USE_INNER_CATEGORIES = true;

    private static final String INNER_CATEGORY_PREFIX = "~";

    protected ProcessPlatforms(String context)
    {
        super(context);
    }

    void process()
    {
        updatePlatforms();

        executeShellScript(OVERWRITE_PLATFORM_IMAGES_SCRIPT);
    }

    private static final String JSON_FILE_EXT = ".json";

    private void updatePlatforms()
    {
        List<PocketCore> cores = pocketCoreDAO().getAllToCopyWithCompleteInformation();

        for(PocketCore core : cores)
        {
            String filepath = config().getPocketPlatformsDirectory() + core.getNamespace() + JSON_FILE_EXT;
            String json = generateJSONforCore(core);

            deleteOldFile(filepath);
            writeToFile(filepath, json);
        }
    }

    public static final String PLATFORM = "platform";

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

        if (core.isUncategorized() && USE_INNER_CATEGORIES)
        {
            String coreName = INNER_CATEGORY_PREFIX + " " + core.getName();

            JsonObject object = tree.getAsJsonObject();
            object.remove("name");
            object.add("name", new JsonPrimitive(coreName));

            tree = object;
        }

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

    public static void main(String args[]) throws Exception
    {
        new ProcessPlatforms(DeploymentConfiguration.LOCAL).run();
    }
}
