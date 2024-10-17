package com.schlock.pocket.app;

import com.google.gson.*;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreCategory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class SetupPocketCoresFromJSON extends AbstractDatabaseApplication
{
    private static final String WORKING_DIR = "_core_zips/platforms/";

    private static final String JSON_FILE_EXT = ".json";

    protected SetupPocketCoresFromJSON(String context)
    {
        super(context);
    }

    void process()
    {
        String WORKING_PATH = config().getPocketUtilityDirectory() + WORKING_DIR;

        FileFilter acceptJsonFiles = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                boolean isJsonFile = pathname.getName().endsWith(JSON_FILE_EXT);
                return pathname.isFile() && isJsonFile;
            }
        };

        File[] jsonFiles = new File(WORKING_PATH).listFiles(acceptJsonFiles);
        if (jsonFiles.length == 0)
        {
            System.out.println("There is nothing to process.");
        }
        else
        {
            for(File jsonFile : jsonFiles)
            {
                try
                {
                    processJsonFile(jsonFile);
                }
                catch (Exception e)
                {
                    System.out.println("Error processing file: " + jsonFile.getName());
                }
            }
        }
    }

    private void processJsonFile(File jsonFile) throws Exception
    {
        String platformId = getCorePlatformIdFromFilename(jsonFile);
        PocketCore core = pocketCoreDAO().getByPlatformId(platformId);
        if (core == null)
        {
            core = getCoreFromJsonFile(jsonFile);
            core.setPlatformId(platformId);

            save(core);
        }
    }

    private PocketCore getCoreFromJsonFile(File jsonFile) throws Exception
    {
        String jsonContents = readFileContents(jsonFile);
        jsonContents = sanitizeContents(jsonContents);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PocketCore.class, new JsonDeserializer<PocketCore>()
                {
                    @Override
                    public PocketCore deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
                    {
                        JsonObject json = element.getAsJsonObject();

                        String name = json.get("name").getAsString();
                        String manufacturer = json.get("manufacturer").getAsString();
                        int year = json.get("year").getAsInt();


                        String categoryName = json.get("category").getAsString();
                        PocketCoreCategory category = pocketCoreCategoryDAO().getByName(categoryName);

                        PocketCore core = new PocketCore();
                        core.setName(name);
                        core.setManufacturer(manufacturer);
                        core.setYear(year);
                        core.setCategory(category);

                        return core;
                    }
                })
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        PocketCore core = gson.fromJson(jsonContents, PocketCore.class);
        return core;
    }

    private String getCorePlatformIdFromFilename(File jsonFile)
    {
        String filename = jsonFile.getName();

        String platformId = filename.substring(0, filename.indexOf(JSON_FILE_EXT));
        return platformId;
    }


    private String sanitizeContents(String jsonContents)
    {
        //Pocket JSON required that the core be wrapped in the "platform" tag
        //But, we want to remove that to process the JSON into GSON

        String PROCESS_TAG = "\"" + ProcessPlatforms.PLATFORM + "\":";
        String FINAL_TAG = "}";

        int start = jsonContents.indexOf(PROCESS_TAG) + PROCESS_TAG.length() + 1;
        int end = jsonContents.lastIndexOf(FINAL_TAG);

        jsonContents = jsonContents.substring(start, end);
        return jsonContents;
    }

    private String readFileContents(File jsonFile) throws Exception
    {
        Path filepath = Path.of(jsonFile.getAbsolutePath());
        String contents = Files.readString(filepath);
        return contents;
    }

    public static void main(String args[]) throws Exception
    {
        // uncomment to use.
        // new SetupPocketCoresFromJSON(DeploymentConfiguration.LOCAL).run();
    }
}
