package com.schlock.pocket.app;

import com.schlock.pocket.entites.MisterMglInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

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
        File favorites = new File(config().getMisterFavoritesDirectory());
        if (favorites.exists())
        {
            eraseFavorites(favorites);
            writeFavorites();
        }
        else
        {
            System.out.println("Favorites folder is missing.");
        }
    }

    private void eraseFavorites(File favorites)
    {
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
        String mainDir = config().getMisterMainDirectory();
        String favoritesDir = config().getMisterFavoritesDirectory();

        for(PocketGame game : pocketGameDAO().getAllAvailableMister())
        {
            if (game.getPlatform().isArcade() || game.getPlatform().isDos())
            {
                File source = new File(mainDir + game.getMisterFilepath());

                String filepath = favoritesDir + MisterMglInfo.getInfo(game).getShortcutFilepath(game);
                File destinationMra = new File(filepath);

                try
                {
                    FileUtils.copyFile(source, destinationMra);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                MisterMglInfo mgl = MisterMglInfo.getInfo(game);
                if (mgl != null)
                {
                    String filepath = favoritesDir + mgl.getShortcutFilepath(game);
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
