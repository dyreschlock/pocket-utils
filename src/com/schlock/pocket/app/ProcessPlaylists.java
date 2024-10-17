package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProcessPlaylists extends AbstractDatabaseApplication
{
    private static final String GAME_JSON_FILENAME = "games.json";
    private static final String CORE_JSON_FILENAME = "cores.json";

    protected ProcessPlaylists(String context)
    {
        super(context);
    }

    void process()
    {
        updateCoreJson();
        updateGameJson();
        copyImagesToWebRepo();
    }

    private void updateCoreJson()
    {
        String coreJson = generateCoreJson();
        String filepath = config().getWebsiteDataDirectory() + CORE_JSON_FILENAME;

        deleteOldFile(filepath);
        writeStringToFile(filepath, coreJson);
    }

    private String generateCoreJson()
    {
        List<JsonObject> jsonObjects = new ArrayList<>();

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

        List<PocketCore> cores = pocketCoreDAO().getAllToCopyWithCompleteInformation();
        for(PocketCore core : cores)
        {
            // I don't want to add an @Expose tag for platformId because it'll cause problems with the ProcessPlatforms task

            JsonObject element = gson.toJsonTree(core).getAsJsonObject();
            element.add("platformId", new JsonPrimitive(core.getPlatformId()));

            jsonObjects.add(element);
        }
        return gson.toJson(jsonObjects);
    }

    private void updateGameJson()
    {
        String gameJson = generateGameJson();
        String filepath = config().getWebsiteDataDirectory() + GAME_JSON_FILENAME;

        deleteOldFile(filepath);
        writeStringToFile(filepath, gameJson);
    }

    private String generateGameJson()
    {
        List<PocketGame> games = pocketGameDAO().getAllAvailable();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PocketCore.class, new JsonSerializer<PocketCore>()
                {
                    @Override
                    public JsonElement serialize(PocketCore src, Type typeOfSrc, JsonSerializationContext context)
                    {
                        //This will output "core": "platformId" rather than the entire category object.
                        String platformId = src.getPlatformId();
                        return new JsonPrimitive(platformId);
                    }
                })
                .registerTypeAdapter(PlatformInfo.class, new JsonSerializer<PlatformInfo>()
                {
                    public JsonElement serialize(PlatformInfo src, Type typeOfSrc, JsonSerializationContext context)
                    {
                        //we don't need the enum name, just the core code, which is where the images are stored
                        String platCode = src.getCoreCode();
                        return new JsonPrimitive(platCode);
                    }
                })
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        return gson.toJson(games);
    }


    private void copyImagesToWebRepo()
    {
        List<PocketGame> games = pocketGameDAO().getAll();
        for(PocketGame game : games)
        {
            if (game.isBoxartConverted())
            {
                String coreCode = game.getPlatform().getCoreCode();

                String bmpLocation = config().getBoxartThumbnailProcessingDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";
                String bmpDestination = config().getWebsiteImageDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";

                File input = new File(bmpLocation);
                File output = new File(bmpDestination);
                output.getParentFile().mkdirs();

                if (!output.exists())
                {
                    try
                    {
                        FileUtils.copyFile(input, output);
                        System.out.println("Image copied for game: " + game.getGameName());
                    }
                    catch (IOException e)
                    {
                        System.out.println("Exception while copying game box art: " + game.getGameName());
                    }
                }
            }
        }
    }

    public static void main(String args[])
    {
        new ProcessPlaylists(DeploymentConfiguration.LOCAL).run();
    }
}
