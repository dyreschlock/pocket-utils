package com.schlock.pocket.entites;

import com.schlock.pocket.services.DeploymentConfiguration;

public enum MisterDrive
{
    SD("/media/fat/"),
    USB1("/media/usb1/");

    String filepath;

    MisterDrive(String filepath)
    {
        this.filepath = filepath;
    }


    protected String getDriveFilepath(PocketCore core)
    {
        return filepath + "games/" + core.getMisterId();
    }

    protected String getLocalFilepath(DeploymentConfiguration config, PocketCore core)
    {
        if (this == SD)
        {
            return config.getMisterMainGamesDirectory() + core.getMisterId();
        }
        if (this == USB1)
        {
            return config.getMisterUSBGamesDirectory() + core.getMisterId();
        }
        return null;
    }
}
