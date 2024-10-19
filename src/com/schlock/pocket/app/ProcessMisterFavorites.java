package com.schlock.pocket.app;

import com.schlock.pocket.entites.MisterMglInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcessMisterFavorites extends AbstractDatabaseApplication
{
    protected ProcessMisterFavorites(String context)
    {
        super(context);
    }

    void process()
    {
        eraseFavorites();
        writeFavorites();
    }

    private void eraseFavorites()
    {
        File favorites = new File(config().getMisterFavoritesDirectory());
        if (favorites.exists())
        {
            for(File folder : favorites.listFiles())
            {
                if (folder.isDirectory())
                {
                    for(File file : folder.listFiles())
                    {
                        file.delete();
                    }
                }
                folder.delete();
            }
            favorites.delete();
        }
    }

    private void writeFavorites()
    {
        String favoritesDir = config().getMisterFavoritesDirectory();

        for(PocketGame game : pocketGameDAO().getAllAvailableMister())
        {
            if (game.getPlatform().isArcade())
            {

            }
            else
            {
                MisterMglInfo mgl = MisterMglInfo.getInfo(game);
                if (mgl != null)
                {
                    String filepath = favoritesDir + mgl.getMglFilepath(game);
                    String contents = mgl.getMglContents(game);

                    File mglFile = new File(filepath);
                    mglFile.getParentFile().mkdirs();

                    try
                    {
                        Files.write(Paths.get(filepath), contents.getBytes());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void main(String[] args)
    {
        new ProcessMisterFavorites(DeploymentConfiguration.LOCAL).run();
    }
}
