package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketCoreInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import com.schlock.pocket.services.database.PocketGameDAO;

import java.io.File;
import java.io.FileFilter;

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

            session.save(core);

            System.out.println("New core created in database: " + namespace);
        }
    }

    private void searchForNewGames()
    {
        try
        {
            for(PocketCoreInfo core : PocketCoreInfo.values())
            {
                searchForNewGames(core);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void searchForNewGames(PocketCoreInfo core)
    {
        if (PocketCoreInfo.ARCADE.equals(core))
        {

        }
        else
        {
            String romLocation = getRomLocation(core);

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

            File coreDirectory = new File(romLocation);
            for(File file : coreDirectory.listFiles(filter))
            {
                processFolder(file, core);
            }
        }
    }

    private String getRomLocation(PocketCoreInfo core)
    {
        String coreCode = core.getCoreCode();
        if (coreCode.contains("/"))
        {
            return config().getPocketAssetsDirectory() + coreCode + "/";
        }
        return config().getPocketAssetsDirectory() + coreCode + "/" + COMMON_FOLDER;
    }

    private void processFolder(File folder, PocketCoreInfo core)
    {
        FileFilter filter = new FileFilter()
        {
            public boolean accept(File file)
            {
                boolean acceptibleFile = false;
                for(String EXT : core.getFileExtensions())
                {
                    if (file.getName().endsWith(EXT))
                    {
                        acceptibleFile = true;
                    }
                }

                boolean notDirectory = !file.isDirectory();
                boolean notDotFile = !file.getName().startsWith(".");

                return notDirectory && acceptibleFile && notDotFile;
            }
        };

        for(File file : folder.listFiles(filter))
        {
            String filename = file.getName();

            PocketGame game = pocketGameDAO().getByFilename(filename);
            if (game == null)
            {
                game = PocketGame.createGame(file, core);

                getSession().save(game);

                System.out.println("New game created in database: " + game.getGameName());
            }
        }
    }

    private PocketGameDAO pocketGameDAO()
    {
        return new PocketGameDAO(session);
    }

    public static void main(String[] args)
    {
        new CreatePocketEntries(DeploymentConfiguration.LOCAL).run();
    }
}
