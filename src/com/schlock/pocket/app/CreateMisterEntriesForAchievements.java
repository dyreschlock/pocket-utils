package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
        createGamesFromOnline();
        createGamesFromLocal();
    }



    private void createGamesFromOnline()
    {
        String wantPlayResponse = getResponse(API_HTTP_REQUEST_WANT_TO_PLAY);
        String completedResponse = getResponse(API_HTTP_REQUEST_COMPLETED);

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

    private void createGamesFromLocal()
    {

    }


    public static void main(String[] args)
    {
        new CreateMisterEntriesForAchievements(DeploymentConfiguration.LOCAL).run();
    }
}
