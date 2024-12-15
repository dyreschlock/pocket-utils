package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UpdateJotegoCores extends AbstractDatabaseApplication
{
    private static final String JOTEGO = "jotego";

    private static final String S16_COMBO = "jts16_c";
    private static final String CZ80_COMBO = "jtcz80_c";

    protected UpdateJotegoCores(String context)
    {
        super(context);
    }

    @Override
    void process()
    {
        List<PocketCore> cores = pocketCoreDAO().getAllToCopyByDev(JOTEGO);

        for(PocketCore core : cores)
        {
            if (S16_COMBO.equalsIgnoreCase(core.getPlatformId()))
            {
                updateSegaComboCore(core);
            }
            else if (CZ80_COMBO.equalsIgnoreCase(core.getPlatformId()))
            {
                updateCapcomComboCore(core);
            }
            else
            {
                updateCore(core);
            }
        }
    }

    private final static String CORE_JSON = "core.json";
    private final static String RBF_EXT = ".rbf_r";

    private void updateCore(PocketCore core)
    {
        String coreFolder = JOTEGO + "." + core.getPlatformId() + "/";

        String sourceCoreFolder = config().getJotegoCoresPath() + coreFolder;
        String destinationCoreFolder = config().getPocketCoresDirectory() + coreFolder;

        String srcJson = sourceCoreFolder + CORE_JSON;
        String destJson = destinationCoreFolder + CORE_JSON;

        boolean successJson = copyFile(srcJson, destJson);

        String srcRbf = sourceCoreFolder + core.getPlatformId() + RBF_EXT;
        String destRbf = destinationCoreFolder + core.getPlatformId() + RBF_EXT;

        boolean successRbf = copyFile(srcRbf, destRbf);

        if (successJson && successRbf)
        {
            System.out.println("Core files successfully copied for: " + core.getPlatformId());
        }
    }

    private void updateCapcomComboCore(PocketCore core)
    {

    }

    private void updateSegaComboCore(PocketCore core)
    {

    }


    private boolean copyFile(String srcPath, String destPath)
    {
        File sourceFile = new File(srcPath);
        File destinationFile = new File(destPath);

        try
        {
            //will overwrite file
            FileUtils.copyFile(sourceFile, destinationFile);

            return true;
        }
        catch (IOException e)
        {
        }
        return false;
    }







    public static void main(String[] args)
    {
        new UpdateJotegoCores(DeploymentConfiguration.LOCAL).run();
    }
}
