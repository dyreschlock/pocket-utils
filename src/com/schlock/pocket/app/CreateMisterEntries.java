package com.schlock.pocket.app;

import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class CreateMisterEntries extends AbstractDatabaseApplication
{
    protected CreateMisterEntries(String context)
    {
        super(context);
    }

    void process()
    {
        searchForNewGamesByCore();
        searchForNewGamesByArcade();
        searchForNewGamesByDOS();
    }


    private void searchForNewGamesByCore()
    {
        try
        {
            for(PocketCore core : pocketCoreDAO().getAllToCopyWithCompleteInformationMister())
            {
                searchForNewGamesByCore(core);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void searchForNewGamesByCore(PocketCore core)
    {
        File gamesFolder = new File(core.getMisterLocalFilepath(config()));
        if (gamesFolder.exists())
        {
            FileFilter filter = new FileFilter()
            {
                @Override
                public boolean accept(File file)
                {
                    boolean directory = file.isDirectory();
                    boolean notIgnore = !file.getName().startsWith("_");

                    return directory && notIgnore;
                }
            };

            for(File folder : gamesFolder.listFiles(filter))
            {
                processFolder(folder, core);
            }
        }
    }

    private void processFolder(File folder, PocketCore core)
    {
        List<PlatformInfo> platforms = PlatformInfo.getByCore(core);
        for(PlatformInfo platform : platforms)
        {
            processFolder(folder, core, platform);
        }
    }

    private void processFolder(File folder, PocketCore core, PlatformInfo platform)
    {
        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                for(String EXT : platform.getFileExtensions())
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

        for(File file : folder.listFiles(filter))
        {
            String filename = file.getName();
            String misterFilepath = core.getMisterRelativeFilepath() + "/" + folder.getName() + "/" + file.getName();

            PocketGame game = pocketGameDAO().getByMisterFilename(filename, core);
            if (game == null)
            {
                game = pocketGameDAO().getByPocketFilename(filename);
                if (game == null)
                {
                    game = PocketGame.createFromMister(file, core, platform, misterFilepath);
                    save(game);

                    System.out.println("New game created in database: " + game.getGameName());
                }
                else if(!game.isAvailableOnMister())
                {
                    game.setMisterFilename(filename);
                    game.setMisterFilepath(misterFilepath);

                    save(game);

                    System.out.println("Updated Pocket game in database: " + game.getGameName());
                }
            }
            else
            {
                if (!game.getMisterFilepath().equals(misterFilepath))
                {
                    game.setMisterFilepath(misterFilepath);
                    save(game);

                    System.out.println("Updating filepath in database: " + game.getGameName());
                }
            }
        }
    }


    private void searchForNewGamesByArcade()
    {
        String arcadeFilepath = config().getMisterArcadeDirectory();

        for(PocketGame game : pocketGameDAO().getByPlatform(PlatformInfo.ARCADE))
        {
            if (game.getMisterFilepath() == null)
            {
                String filename = game.getMisterFilename();
                if (filename == null)
                {
                    filename = game.getPocketFilename();
                    filename = filename.substring(0, filename.indexOf(".json")) + ".mra";
                }

                String filepath = arcadeFilepath + filename;
                String misterFilepath = "_Arcade/" + filename;

                File gameFile = new File(filepath);
                if (gameFile.exists())
                {
                    game = PocketGame.updateFromMisterArcade(game, gameFile, misterFilepath);

                    save(game);

                    System.out.println("Updated Arcade game in database: " + game.getGameName());
                }
            }
        }
    }

    private void searchForNewGamesByDOS()
    {

    }

    public static void main(String[] args)
    {
        new CreateMisterEntries(DeploymentConfiguration.LOCAL).run();
    }
}
