package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.lang.reflect.Type;
import java.util.List;

public class ProcessPlatforms extends AbstractDatabaseApplication
{
    private static final boolean USE_INNER_CATEGORIES = true;

    private static final String INNER_CATEGORY_NEG_PREFIX = "★";
    private static final String INNER_CATEGORY_POS_PREFIX = " ";

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
    private static final String ARCHIVE_PATH = "_archive/";

    private void updatePlatforms()
    {
        int count = 0;

        List<PocketCore> cores = pocketCoreDAO().getAllToCopyReleaseWithCompleteInformation();
        for(PocketCore core : cores)
        {
            String filename = core.getPlatformId() + JSON_FILE_EXT;

            String filepath = config().getPocketPlatformsDirectory();

            deleteOldFile(filepath + filename);

            if (!core.isCopy() && !core.isFavorite())
            {
                filepath += ARCHIVE_PATH;
                deleteOldFile(filepath + filename);
            }

            String json = generateJSONforCore(core);
            writeStringToFile(filepath + filename, json);

            count++;
        }

        System.out.println("Number of Platform JSON written: " + count);
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

        if (USE_INNER_CATEGORIES)
        {
            String coreName = core.getName();
            if (!core.isFavorite())
            {
                coreName = INNER_CATEGORY_NEG_PREFIX + coreName;
            }

            JsonObject object = tree.getAsJsonObject();
            object.remove("name");
            object.add("name", new JsonPrimitive(coreName));

            tree = object;
        }

        base.add(PLATFORM, tree);
        return gson.toJson(base);
    }


    public static void main(String args[]) throws Exception
    {
        new ProcessPlatforms(DeploymentConfiguration.LOCAL).run();
    }
}
