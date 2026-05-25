package com.schlock.pocket.app;

import com.schlock.pocket.entites.MisterDrive;
import com.schlock.pocket.entites.MisterMglInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcessMisterShortcuts extends AbstractDatabaseApplication
{
    public static final String FAVORITES_FOLDER = "_TapTo";

    protected ProcessMisterShortcuts(String context)
    {
        super(context);
    }

    void process()
    {
        processFavorites();
        processAchievements();
    }

    void processFavorites()
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

    void processAchievements()
    {
        File achievements = new File(config().getMisterAchievementsDirectory());
        if (achievements.exists())
        {
            eraseAchievements(achievements);
            writeAchievements();
        }
        else
        {
            System.out.println("Achievements folder is missing.");
        }
    }

    private void eraseFavorites(File favorites)
    {
        String favfavFilepath = favorites.getParentFile().getAbsolutePath() + FAVORITES_FOLDER;

        deleteFolderAndContents(favorites);
        deleteFolderAndContents(new File(favfavFilepath));
    }

    private void eraseAchievements(File achievements)
    {
        String taptoFilepath = achievements.getAbsolutePath() + FAVORITES_FOLDER;

        deleteFolderAndContents(new File(taptoFilepath));
        deleteFolderAndContents(achievements);
    }

    private void deleteFolderAndContents(File sourceFolder)
    {
        if (sourceFolder.exists())
        {
            for(File folder : sourceFolder.listFiles())
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
            sourceFolder.delete();
        }
    }

    private void writeFavorites()
    {
        String favorites = config().getMisterFavoritesDirectory();
        String favfavFilepath = new File(favorites).getParentFile().getAbsolutePath() + "/" + FAVORITES_FOLDER + "/";

        String mainDir = config().getMisterMainDirectory();

        for(PocketGame game : pocketGameDAO().getAllAvailableMister())
        {
            if (game.getPlatform().isArcade())
            {
            }
            else if (game.getPlatform().isDos())
            {
                File source = new File(mainDir + game.getMisterFilepath());

                String filepath = favorites + MisterMglInfo.getInfo(game).getShortcutFilepath(game);
                copySourceFileToDestination(source, filepath);

                if (game.isFavorite())
                {
                    filepath = favfavFilepath + MisterMglInfo.getInfo(game).getShortcutFilepath(game);
                    copySourceFileToDestination(source, filepath);
                }
            }
            else
            {
                MisterMglInfo mgl = MisterMglInfo.getInfo(game);
                if (mgl != null && MisterDrive.SD.equals(game.getCore().getMisterDrive()))
                {
                    String contents = mgl.getMglContents(game);

                    String filepath = favorites + mgl.getShortcutFilepath(game);
                    writeContentsToDestination(contents, filepath);

                    if (game.isFavorite())
                    {
                        filepath = favfavFilepath + mgl.getShortcutFilepath(game);
                        writeContentsToDestination(contents, filepath);
                    }
                }
            }
        }
    }

    private void writeAchievements()
    {
        String achievements = config().getMisterAchievementsDirectory();

        for(PocketGame game : pocketGameDAO().getAllMisterWithAchievements())
        {
            MisterMglInfo mgl = MisterMglInfo.getInfo(game);
            if (mgl != null && mgl.isAchievementsOk())
            {
                String filename = mgl.getShortcutFilename(game);
                boolean softcore = !game.getPlatform().isAchievementHardcore();

                String contents = mgl.getMglContents(game);
                String filepath = achievements + game.getAchievementLevel().getAchievementFolderRelativeFilepath(filename, softcore);

                writeContentsToDestination(contents, filepath);

                if (game.getAchievementLevel().isCopyForTapTo() && game.getPlatform().isAchievementHardcore())
                {
                    filepath = achievements + game.getAchievementLevel().getAchievementFolderTapToRelativeFilepath(filename);
                    writeContentsToDestination(contents, filepath);
                }
            }
        }
    }

    private void copySourceFileToDestination(File source, String destination)
    {
        File destinationFile = new File(destination);
        destinationFile.getParentFile().mkdirs();

        try
        {
            FileUtils.copyFile(source, destinationFile);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void writeContentsToDestination(String contents, String destination)
    {
        File mglFile = new File(destination);
        mglFile.getParentFile().mkdirs();
        mglFile.getParentFile().getParentFile().mkdirs();

        try
        {
            Files.write(Paths.get(destination), contents.getBytes());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        new ProcessMisterShortcuts(DeploymentConfiguration.LOCAL).run();
    }
}
