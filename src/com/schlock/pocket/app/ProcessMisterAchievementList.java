package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

public class ProcessMisterAchievementList extends AbstractDatabaseApplication
{
    protected ProcessMisterAchievementList(String context)
    {
        super(context);
    }

    void process()
    {

    }




    public static void main(String[] args)
    {
        new ProcessMisterAchievementList(DeploymentConfiguration.LOCAL).run();
    }
}
