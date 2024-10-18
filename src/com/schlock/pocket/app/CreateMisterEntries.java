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
    private enum MisterLocation
    {
        MISTER_SD("/media/fat/"),
        MISTER_USB("/media/usb1/");

        String filepath;

        MisterLocation(String filepath)
        {
            this.filepath = filepath;
        }

        public String getMisterFilepath(PocketCore core)
        {
            return filepath + "games/" + core.getMisterId();
        }

        public String getLocalFilepath(DeploymentConfiguration config, PocketCore core)
        {
            if (this == MISTER_SD)
            {
                return config.getMisterMainGamesDirectory() + core.getMisterId();
            }
            if (this == MISTER_USB)
            {
                return config.getMisterUSBGamesDirectory() + core.getMisterId();
            }
            return null;
        }
    }

    protected CreateMisterEntries(String context)
    {
        super(context);
    }

    void process()
    {
        searchForNewGamesByCore();
        searchForNewGamesByArcade();
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
        MisterLocation location = getMisterLocation(core);
        if (location != null)
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

            File gamesFolder = new File(location.getLocalFilepath(config(), core));
            for(File folder : gamesFolder.listFiles(filter))
            {
                processFolder(folder, location, core);
            }
        }
    }

    private void processFolder(File folder, MisterLocation location, PocketCore core)
    {
        List<PlatformInfo> platforms = PlatformInfo.getByCoreCode(core);
        for(PlatformInfo platform : platforms)
        {
            processFolder(folder, location, core, platform);
        }
    }

    private void processFolder(File folder, MisterLocation location, PocketCore core, PlatformInfo platform)
    {
        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                for(String EXT : platform.getFileExtensions())
                {
                    if (file.getName().endsWith(EXT) &&
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
            String misterFilepath = location.getMisterFilepath(core) + "/" + folder.getName() + "/" + file.getName();

            PocketGame game = pocketGameDAO().getByMisterFilename(filename);
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
        }
    }

    private MisterLocation getMisterLocation(PocketCore core)
    {
        for(MisterLocation location : MisterLocation.values())
        {
            String path = location.getLocalFilepath(config(), core);
            if (new File(path).exists())
            {
                return location;
            }
        }
        return null;
    }


    private void searchForNewGamesByArcade()
    {

    }

    public static void main(String[] args)
    {
        new CreateMisterEntries(DeploymentConfiguration.LOCAL).run();
    }
}
