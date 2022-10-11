package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
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
        try
        {
            for(PocketCore core : PocketCore.values())
            {
                processCore(core);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processCore(PocketCore core)
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

    private String getRomLocation(PocketCore core)
    {
        String coreCode = core.getCoreCode();
        if (coreCode.contains("/"))
        {
            return config().getPocketAssetsDirectory() + coreCode + "/";
        }
        return config().getPocketAssetsDirectory() + coreCode + "/" + COMMON_FOLDER;
    }

    private void processFolder(File folder, PocketCore core)
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
