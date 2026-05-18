package com.schlock.pocket.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.schlock.pocket.entites.AchievementEntry;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CreateMisterEntriesForAchievements extends AbstractDatabaseApplication
{
    /**
     * https://api-docs.retroachievements.org/v1/get-user-want-to-play-list.html
     * https://api-docs.retroachievements.org/v1/get-user-completed-games.html
     */
    private static String API_HTTP_REQUEST_WANT_TO_PLAY = "https://retroachievements.org/API/API_GetUserWantToPlayList.php?u=%s&y=%s";
    private static String API_HTTP_REQUEST_COMPLETED = "https://retroachievements.org/API/API_GetUserCompletedGames.php?u=%s&y=%s";


    protected CreateMisterEntriesForAchievements(String context)
    {
        super(context);
    }

    void process()
    {
        careteGamesFormOnlineWantPlayList();
        createGamesFromOnlineCompletedList();
        createGamesFromLocal();
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
        // search for existing

        // create
        // search for game locally

        String temp = "";
    }


    private void createGamesFromLocal()
    {

    }


    private class WantPlayResults
    {
        @Expose
        public List<AchievementEntry> Results;
    }



    public static void main(String[] args)
    {
        new CreateMisterEntriesForAchievements(DeploymentConfiguration.LOCAL).run();
    }
}
