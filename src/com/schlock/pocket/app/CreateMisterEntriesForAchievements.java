package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

public class CreateMisterEntriesForAchievements extends AbstractDatabaseApplication
{
    protected CreateMisterEntriesForAchievements(String context)
    {
        super(context);
    }

    void process()
    {

    }




    public static void main(String[] args)
    {
        new CreateMisterEntriesForAchievements(DeploymentConfiguration.LOCAL).run();
    }
}
