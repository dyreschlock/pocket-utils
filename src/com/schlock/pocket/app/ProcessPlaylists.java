package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
        generateGameJson();
        copyBoxartImagesToWebRepo();
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

        return gson.toJson(cores);
    }



    private void generateGameJson()
    {
        config().getWebsiteDataDirectory();
    }

    private void copyBoxartImagesToWebRepo()
    {
        List<PocketGame> games = pocketGameDAO().getAll();
        for(PocketGame game : games)
        {
            if (game.isInLibrary())
            {
                String coreCode = game.getPlatform().getCoreCode();

                String bmpLocation = config().getProcessingLibraryDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";
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
