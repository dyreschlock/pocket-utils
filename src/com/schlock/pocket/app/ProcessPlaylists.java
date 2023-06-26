package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProcessPlaylists extends AbstractDatabaseApplication
{
    protected ProcessPlaylists(String context)
    {
        super(context);
    }

    void process()
    {
        generateCoreJson();
        generateGameJson();
        copyBoxartImagesToWebRepo();
    }

    private void generateCoreJson()
    {

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
