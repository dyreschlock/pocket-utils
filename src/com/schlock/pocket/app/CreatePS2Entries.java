package com.schlock.pocket.app;

import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PlaystationGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;
import java.io.FilenameFilter;

public class CreatePS2Entries extends AbstractDatabaseApplication
{
    private static final String PS2_FOLDER = "DVD";
    private static final String PS1_FOLDER = "POPS";

    private static final String ART_FOLDER = "ART";
    private static final String CFG_FOLDER = "CFG";

    protected CreatePS2Entries(String context)
    {
        super(context);
    }

    void process()
    {
        final String PS2_LOCATION = config().getPlaystationDirectory() + PS2_FOLDER;
        final String PS1_LOCATION = config().getPlaystationDirectory() + PS1_FOLDER;

        createEntriesFromLocation(PS2_LOCATION, PlatformInfo.PS2);
        createEntriesFromLocation(PS1_LOCATION, PlatformInfo.PS1);
    }

    private void createEntriesFromLocation(String location, PlatformInfo platform)
    {
        FilenameFilter filenameFilter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                final String ISO = ".iso";
                final String VCD = ".vcd";

                boolean filetypeOk = name.toLowerCase().endsWith(ISO) || name.toLowerCase().endsWith(VCD);

                return !name.startsWith(".") && filetypeOk;
            }
        };

        for(File gameFile : new File(location).listFiles(filenameFilter))
        {
            PlaystationGame gameData = PlaystationGame.create(gameFile, platform);

            PlaystationGame game = playstationGameDAO().getByGameId(gameData.getGameId());
            if (game == null)
            {
                game = gameData;
            }

            boolean art = checkForFile(ART_FOLDER, game);
            boolean cfg = checkForFile(CFG_FOLDER, game);

            game.setHaveArt(art);
            game.setHaveCfg(cfg);
//            game.setCopied(true);

            save(game);
        }
    }

    private boolean checkForFile(final String FOLDER_NAME, PlaystationGame game)
    {
        String folderPath = config().getPlaystationDirectory() + FOLDER_NAME;

        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith(game.getGameId());
            }
        };

        for(File file : new File(folderPath).listFiles(filter))
        {
            return true;
        }
        return false;
    }


    public static void main(String args[])
    {
        new CreatePS2Entries(DeploymentConfiguration.LOCAL).run();
    }
}
