package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class CreatePocketEntries extends AbstractDatabaseApplication
{
    protected CreatePocketEntries(String context)
    {
        super(context);
    }

    void process()
    {
        searchForNewCores();
        searchForNewGames();
    }

    private void searchForNewCores()
    {
        File assetDirectory = new File(config().getPocketAssetsDirectory());

        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                boolean isDirectory = file.isDirectory();
                return isDirectory;
            }
        };

        for(File directory : assetDirectory.listFiles(filter))
        {
            String namespace = directory.getName();
            createCoreObjectIfDoesntExist(namespace);
        }
    }

    private void createCoreObjectIfDoesntExist(String namespace)
    {
        PocketCore core = pocketCoreDAO().getByNamespace(namespace);
        if (core == null)
        {
            core = new PocketCore();
            core.setNamespace(namespace);

            save(core);

            System.out.println("New core created in database: " + namespace);
        }
    }

    private void searchForNewGames()
    {
        try
        {
            for(PocketCore core : pocketCoreDAO().getAllWithCompleteInformation())
            {
                searchForNewGames(core);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void searchForNewGames(PocketCore core)
    {
        String romLocation = getRomLocationAbsolutePath(core);
        File coreRomsDirectory = new File(romLocation);

        if (core.isRomsSorted())
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

            for(File folder : coreRomsDirectory.listFiles(filter))
            {
                processFolder(folder, core);
            }
        }
        else
        {
            processFolder(coreRomsDirectory, core);
        }
    }

    private void processFolder(File folder, PocketCore core)
    {
        List<PlatformInfo> platforms = PlatformInfo.getByCoreCode(core);
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
                boolean acceptableFile = false;
                for(String EXT : platform.getFileExtensions())
                {
                    if (file.getName().endsWith(EXT))
                    {
                        acceptableFile = true;
                    }
                }

                boolean notDirectory = !file.isDirectory();
                boolean notDotFile = !file.getName().startsWith(".");

                return notDirectory && acceptableFile && notDotFile;
            }
        };

        for(File file : folder.listFiles(filter))
        {
            String filename = file.getName();

            PocketGame game = pocketGameDAO().getByFilename(filename);
            if (game == null)
            {
                game = PocketGame.createGame(file, core, platform);
                save(game);

                System.out.println("New game created in database: " + game.getGameName());
            }
        }
    }

    public static void main(String[] args)
    {
        new CreatePocketEntries(DeploymentConfiguration.LOCAL).run();
    }
}
