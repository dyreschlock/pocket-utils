package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatePocketEntries extends AbstractDatabaseApplication
{
    private static final String UNSORTED_FOLDER = "__unsorted";
    private static final String UNSORTED_FILENAME = "unsorted_games.txt";

    private List<String> unsortedGames = new ArrayList<>();

    protected CreatePocketEntries(String context)
    {
        super(context);
    }

    void process()
    {
        searchForNewCores();
        searchForNewGames();

        writeUnsortedGames();
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
            for(PocketCore core : pocketCoreDAO().getAllToCopyWithCompleteInformation())
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
        if (!coreRomsDirectory.exists())
        {
            System.out.println("Directories don't exist for core: " + core.getNamespace());
            return;
        }

        if (core.isRomsSorted())
        {
            FileFilter filter = new FileFilter()
            {
                @Override
                public boolean accept(File file)
                {
                    boolean directory = file.isDirectory();
                    boolean notIgnore = !file.getName().startsWith("_");

                    boolean unsorted = UNSORTED_FOLDER.equals(file.getName());

                    return unsorted || (directory && notIgnore);
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

            if (UNSORTED_FOLDER.equals(folder.getName()))
            {
                String gameEntry = core.getNamespace() + " / " + filename;
                if (!unsortedGames.contains(gameEntry))
                {
                    unsortedGames.add(gameEntry);
                }
            }
            else
            {
                PocketGame game = pocketGameDAO().getByFilename(filename);
                if (game == null)
                {
                    game = PocketGame.createGame(file, core, platform);
                    save(game);

                    System.out.println("New game created in database: " + game.getGameName());
                }
            }
        }
    }

    private void writeUnsortedGames()
    {
        String totalGamesMessage = "Number of Unsorted Games: " + unsortedGames.size();
        System.out.println(totalGamesMessage);

        String filepath = config().getPocketUtilityDirectory() + UNSORTED_FILENAME;
        File unsortedFile = new File(filepath);
        if (unsortedFile.exists())
        {
            System.out.println("Unsorted Games file already exists.");
        }
        else
        {
            Collections.sort(unsortedGames);

            try
            {
                BufferedWriter writer = new BufferedWriter(new FileWriter(unsortedFile));

                writer.write(totalGamesMessage);
                writer.newLine();
                writer.newLine();

                for(String game : unsortedGames)
                {
                    writer.write(game);
                    writer.newLine();
                }

                writer.flush();
                writer.close();

                System.out.println("Unsorted Games file written.");
            }
            catch (Exception e)
            {
                System.err.println("Problem writing Unsorted Games file.");
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args)
    {
        new CreatePocketEntries(DeploymentConfiguration.LOCAL).run();
    }
}
