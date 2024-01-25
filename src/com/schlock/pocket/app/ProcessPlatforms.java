package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class ProcessPlatforms extends AbstractDatabaseApplication
{
    private static final boolean USE_INNER_CATEGORIES = true;

    private static final String INNER_CATEGORY_NEG_PREFIX = "★";
    private static final String INNER_CATEGORY_POS_PREFIX = " ";

    private static final String IMAGES_RELEASED_ARCADE_FILE = "images_released_arcade.md";
    private static final String IMAGES_UNRELEASED_FILE = "images_unreleased.md";

    protected ProcessPlatforms(String context)
    {
        super(context);
    }

    void process()
    {
        updatePlatforms();

        executeShellScript(OVERWRITE_PLATFORM_IMAGES_SCRIPT);

//        updateImageReadmeFiles();
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
            writeStringToFile(filepath, json);
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


    private void updateImageReadmeFiles()
    {
        try
        {
            String releasedArcadeContents = "";
            String unreleasedCoreContents = "";

            updateFile(IMAGES_RELEASED_ARCADE_FILE, releasedArcadeContents);
            updateFile(IMAGES_UNRELEASED_FILE, unreleasedCoreContents);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }






    private void updateFile(String filename, String newContents) throws Exception
    {
        File baseFile = new File(config().getDataDirectory() + filename);

        StringBuilder contents = readFileContents(baseFile);

        File updatedFile = new File(config().getPlatformImagesDirectory() + filename);

        String fullContents = contents.toString() + newContents;

        writeStringToFile(updatedFile.getAbsolutePath(), fullContents);
    }

    private StringBuilder readFileContents(File file) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder contents = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null)
        {
            contents.append(line);
            contents.append("\r\n");
        }
        return contents;
    }

    public static void main(String args[]) throws Exception
    {
        new ProcessPlatforms(DeploymentConfiguration.LOCAL).run();
    }
}
