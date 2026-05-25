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
     * https://api-docs.retroachievements.org/v1/get-user-completed-games.html
     */
    private static String API_HTTP_REQUEST_WANT_TO_PLAY = "https://retroachievements.org/API/API_GetUserWantToPlayList.php?u=%s&y=%s";
    private static String API_HTTP_REQUEST_COMPLETED = "https://retroachievements.org/API/API_GetUserCompletedGames.php?u=%s&y=%s";


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
        careteGamesFormOnlineWantPlayList();
        createGamesFromOnlineCompletedList();
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


    private void careteGamesFormOnlineWantPlayList()
    {
        Gson gson = new GsonBuilder().create();
        Type wantPlayResults = new TypeToken<WantPlayResults>(){}.getType();

        String wantPlayResponse = getResponse(API_HTTP_REQUEST_WANT_TO_PLAY);
        WantPlayResults results = gson.fromJson(wantPlayResponse, wantPlayResults);

        processEntries(results.Results);
    }

    private void createGamesFromOnlineCompletedList()
    {
        Gson gson = new GsonBuilder().create();
        Type completedEntries = new TypeToken<ArrayList<AchievementEntry>>(){}.getType();

        String completedResponse = getResponse(API_HTTP_REQUEST_COMPLETED);
        List<AchievementEntry> games = gson.fromJson(completedResponse, completedEntries);
        processEntries(games);
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
            if ("Events".equals(consoleName) && entry.isHardcore())
            {
                System.out.println("Event: " + entry.getTitle());
                continue;
            }

            PlatformInfo platform = PlatformInfo.getByAchievementTitle(entry.getConsoleName());
            if (platform != null && (
                            (entry.isWantToPlay()) ||
                                    (platform.isAchievementHardcore() && entry.isHardcore()) ||
                                    (!platform.isAchievementHardcore() && !entry.isHardcore())))
            {
                boolean save = false;

                PocketGame game = locateGame(entry, platform);
                if (game == null)
                {
                    PocketCore core = pocketCoreDAO().getByPlatformId(platform.getPlatformId());

                    game = PocketGame.createAchievementGame(entry, core, platform);
                    save = true;

                    System.out.println("New Game Created: " + entry.getTitle());
                }

                if (game.getAchievementId() == null)
                {
                    game.setAchievementId(entry.getId());
                    save = true;
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

                        save = true;
                    }
                }

                if (game.getAchievementTitle() == null)
                {
                    game.setAchievementTitle(entry.getTitle());
                    save = true;
                }

                if (game.getAchievementLevel() == null)
                {
                    if (entry.isWantToPlay())
                    {
                        game.setAchievementLevel(AchievementLevel.UNSTARTED);
                    }
                    else
                    {
                        game.setAchievementLevel(AchievementLevel.UNFINISHED);
                    }
                    save = true;
                }
                else if (entry.isMastered() && !game.getAchievementLevel().isMastered())
                {
                    game.setAchievementLevel(AchievementLevel.MASTERED);
                    save = true;
                }
                else if(!entry.isWantToPlay() && game.getAchievementLevel().equals(AchievementLevel.UNSTARTED))
                {
                    game.setAchievementLevel(AchievementLevel.UNFINISHED);
                    save = true;
                }


                if (save)
                {
                    System.out.println("Updating Game: " + game.getTitle());
                    save(game);
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

        String standardName = getStandardName(entry.getTitle());
        game = pocketGameDAO().getByTitleFilenameContains(standardName, platform);
        if (game != null)
        {
            return game;
        }

        standardName = getBaseName(standardName);
        game = pocketGameDAO().getByTitleFilenameContains(standardName, platform);
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
        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                if (file.isDirectory())
                {
                    return true;
                }
                for(String EXT : game.getPlatform().getFileExtensions())
                {
                    if (file.getName().toLowerCase().endsWith(EXT) &&
                            !file.getName().startsWith("."))
                    {
                        return true;
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

                String entityTitle = getStandardName(game.getAchievementTitle());
                if (filename.startsWith(entityTitle))
                {
                    return file;
                }

                entityTitle = getBaseName(entityTitle);
                if (filename.startsWith(entityTitle))
                {
                    return file;
                }


            }
        }
        return null;
    }


    private String getStandardName(String entryTitle)
    {
        String standardName = entryTitle;
        if (standardName.contains("~"))
        {
            standardName = standardName.split("~")[2].trim();
        }
        return standardName;
    }

    private String getBaseName(String entryTitle)
    {
        final String THE = "The";

        String baseName = entryTitle;
        if (baseName.startsWith(THE))
        {
            baseName = baseName.substring(THE.length()).trim();
        }
        return baseName;
    }

    private class WantPlayResults
    {
        @Expose
        public List<AchievementEntry> Results;
    }



    public static void main(String[] args)
    {
        new CreateMisterEntriesFromRAOnline(DeploymentConfiguration.LOCAL).run();
    }
}
