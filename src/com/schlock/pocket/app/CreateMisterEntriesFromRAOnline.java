package com.schlock.pocket.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.schlock.pocket.entites.*;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class CreateMisterEntriesFromRAOnline extends AbstractDatabaseApplication
{
    /**
     * https://api-docs.retroachievements.org/v1/get-user-want-to-play-list.html
     * https://api-docs.retroachievements.org/v1/get-user-completion-progress.html
     */
    private static String API_HTTP_REQUEST_WANT_TO_PLAY = "https://retroachievements.org/API/API_GetUserWantToPlayList.php?u=%s&y=%s";
    private static String API_HTTP_REQUEST_COMPLETED = "https://retroachievements.org/API/API_GetUserCompletionProgress.php?u=%s&y=%s";


    protected CreateMisterEntriesFromRAOnline(String context)
    {
        super(context);
    }

    void process()
    {
//        try
//        {
//            getMappings();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }


        createGamesFormOnlineList(API_HTTP_REQUEST_WANT_TO_PLAY);
        createGamesFormOnlineList(API_HTTP_REQUEST_COMPLETED);
    }

    private void getMappings() throws Exception
    {
        String serviceUrl = "http://192.168.50.111:7497/api";

        TapToRequest requestContent = new TapToRequest();
        requestContent.jsonrpc = "2.0";
        requestContent.id = "4b5da056-a5d4-436b-b4e6-b96231e99969";
        requestContent.method = "mappings";

        Gson gson = new GsonBuilder().create();

        String requestDetails = gson.toJson(requestContent).toString();


        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .POST(HttpRequest.BodyPublishers.ofString(requestDetails))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    private class TapToRequest
    {
        @Expose
        public String jsonrpc;
        @Expose
        public String id;
        @Expose
        public String method;
    }


    private void createGamesFormOnlineList(final String URL)
    {
        Gson gson = new GsonBuilder().create();
        Type resultsType = new TypeToken<ResultsObject>(){}.getType();

        String response = getResponse(URL);
        ResultsObject results = gson.fromJson(response, resultsType);

        processEntries(results.Results);
    }

    private String getResponse(final String baseURL)
    {
        try
        {
            String username = config().getAchievementsUsername();
            String apikey = config().getAchievementsApiKey();

            URL url = new URL(String.format(baseURL, username, apikey));
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer result = new StringBuffer();

            String inputLine;
            while((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }
            in.close();

            return result.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void processEntries(List<AchievementEntry> entries)
    {
        for(AchievementEntry entry : entries)
        {
            String consoleName = entry.getConsoleName();
            if ("Events".equals(consoleName))
            {
                System.out.println("Event: " + entry.getTitle());
                continue;
            }

            PlatformInfo platform = PlatformInfo.getByAchievementTitle(entry.getConsoleName());
            if (platform != null)
            {
                List<String> updateMessages = new ArrayList<>();

                PocketGame game = locateGame(entry, platform);
                if (game == null)
                {
                    PocketCore core = pocketCoreDAO().getByPlatformId(platform.getPlatformId());

                    game = PocketGame.createAchievementGame(entry, core, platform);

                    updateMessages.add("New Game Created: " + game.getTitle());
                }

                if (game.getMisterFilepath() == null)
                {
                    File file = locateFile(game);
                    if (file != null)
                    {
                        String filename = file.getName();
                        game.setMisterFilename(filename);

                        String filepath = file.getAbsolutePath();
                        filepath = filepath.substring(config().getMisterMainDirectory().length());

                        game.setMisterFilepath(filepath);

                        updateMessages.add(String.format("Update on %s: Game found at %s", game.getTitle(), filepath));
                    }
                }

                if (game.getAchievementId() == null || game.getAchievementTitle() == null)
                {
                    game.setAchievementId(entry.getId());
                    game.setAchievementTitle(entry.getTitle());

                    updateMessages.add(String.format("Update on %s: RA Title and ID added", game.getTitle()));
                }

                if (entry.isWantToPlay())
                {
                    if (game.getAchievementLevel() == null)
                    {
                        game.setAchievementLevel(AchievementLevel.UNSTARTED);
                        updateMessages.add(String.format("Update on %s: Achievement set to %s", game.getTitle(), game.getAchievementLevel().name()));
                    }
                }
                else
                {
                    boolean save = false;
                    if (entry.isMastered())
                    {
                        if(!game.getAchievementLevel().isMastered())
                        {
                            game.setAchievementLevel(AchievementLevel.MASTERED);
                            save = true;
                        }
                    }
                    else if (entry.isBeaten() && game.getAchievementLevel().equals(AchievementLevel.STARTED))
                    {
                        game.setAchievementLevel(AchievementLevel.BEATEN);
                        save = true;
                    }
                    else if (entry.isHasProgress() &&
                            (game.getAchievementLevel() == null || game.getAchievementLevel().equals(AchievementLevel.UNSTARTED)))
                    {
                        game.setAchievementLevel(AchievementLevel.STARTED);
                        save = true;
                    }
                    else if (entry.isHasProgress() && game.getAchievementLevel().equals(AchievementLevel.UNSTARTED_PICROSS))
                    {
                        game.setAchievementLevel(AchievementLevel.CURRENT);
                        save = true;
                    }

                    if(save)
                    {
                        updateMessages.add(String.format("Update on %s: Achievement set to %s", game.getTitle(), game.getAchievementLevel().name()));
                    }
                }


                if (!updateMessages.isEmpty())
                {
                    save(game);

                    for(String message : updateMessages)
                    {
                        System.out.println(message);
                    }
                }
            }
        }
    }

    private PocketGame locateGame(AchievementEntry entry, PlatformInfo platform)
    {
        PocketGame game = pocketGameDAO().getByAchievementTitle(entry.getTitle(), platform);
        if (game != null)
        {
            return game;
        }

        String gameTitle = removeTildaCategory(entry.getTitle());
        game = pocketGameDAO().getByTitleFilenameContains(gameTitle, platform);
        if (game != null)
        {
            return game;
        }

        gameTitle = removeThe(gameTitle);
        game = pocketGameDAO().getByTitleFilenameContains(gameTitle, platform);
        if (game != null)
        {
            return game;
        }

        gameTitle = convertColon(gameTitle);
        game = pocketGameDAO().getByTitleFilenameContains(gameTitle, platform);
        if (game != null)
        {
            return game;
        }



        //TODO more methods of searching for game

        return game;
    }

    private File locateFile(PocketGame game)
    {
        String filepathAllGames = game.getCore().getMisterLocalFilepath(config()) + "/_all";
        File allGames = new File(filepathAllGames);
        if (allGames.exists())
        {
            FileFilter filter = new FileFilter()
            {
                public boolean accept(File file)
                {
                    boolean directory = file.isDirectory();
                    boolean accept = !file.getName().startsWith(".");

                    return directory && accept;
                }
            };

            List<File> files = Arrays.asList(allGames.listFiles(filter));
            Collections.sort(files, new Comparator<File>()
            {
                public int compare(File o1, File o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });


            File gameFile;
            for(File folder : files)
            {
                gameFile = locateFile(folder, game);
                if (gameFile != null)
                {
                    return gameFile;
                }
            }
        }
        return null;
    }

    private File locateFile(File folder, PocketGame game)
    {
        if (game.getMisterFilename() != null)
        {
            String filepath = folder.getAbsolutePath() + "/" + game.getMisterFilename();
            File file = new File(filepath);
            if (file.exists())
            {
                return file;
            }
        }

        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                if (!file.getName().startsWith("."))
                {
                    if (file.isDirectory())
                    {
                        return true;
                    }
                    if (game.getMisterFilename() == null)
                    {
                        for(String EXT : game.getPlatform().getFileExtensions())
                        {
                            if (file.getName().toLowerCase().endsWith(EXT))
                            {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };

        List<File> files = Arrays.asList(folder.listFiles(filter));
        Collections.sort(files, new Comparator<File>()
        {
            public int compare(File o1, File o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });


        for(File file : files)
        {
            if (file.isDirectory())
            {
                File result = locateFile(file, game);
                if (result != null)
                {
                    return result;
                }
            }
            else
            {
                //check filename for matches

                String filename = file.getName();

                if (filename.equals(game.getMisterFilename()))
                {
                    return file;
                }

                if (filename.startsWith(game.getTitle()))
                {
                    return file;
                }

                String entityTitle = removeTildaCategory(game.getAchievementTitle());
                if (filename.startsWith(entityTitle))
                {
                    return file;
                }

                entityTitle = removeThe(entityTitle);
                if (filename.startsWith(entityTitle))
                {
                    return file;
                }

                entityTitle = convertColon(entityTitle);
                if (filename.startsWith(entityTitle))
                {
                    return file;
                }



            }
        }
        return null;
    }


    private String removeTildaCategory(String entryTitle)
    {
        String standardName = entryTitle;
        while(standardName.startsWith("~"))
        {
            standardName = standardName.substring(1);
            standardName = standardName.substring(standardName.indexOf("~") + 1);
        }
        return standardName;
    }

    private String removeThe(String entryTitle)
    {
        final String THE = "The";

        String baseName = entryTitle;
        if (baseName.startsWith(THE))
        {
            baseName = baseName.substring(THE.length()).trim();
        }
        return baseName;
    }

    private String convertColon(String entryTitle)
    {
        return entryTitle.replaceAll(":", " -");
    }

    private class ResultsObject
    {
        @Expose
        public List<AchievementEntry> Results;
    }



    public static void main(String[] args)
    {
        new CreateMisterEntriesFromRAOnline(DeploymentConfiguration.LOCAL).run();
    }
}
