package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;
import com.mysql.jdbc.StringUtils;

public class AchievementEntry
{
    @Expose
    private String GameID;

    @Expose
    private String ID;

    @Expose
    private String Title;

    @Expose
    private String ConsoleName;

    @Expose
    private String PctWon;

    @Expose
    private String HardcoreMode;



    public boolean isMastered()
    {
        return PctWon != null && PctWon.equals("1.0000");
    }

    public boolean isHardcore()
    {
        return HardcoreMode != null && HardcoreMode.equals("1");
    }

    public boolean isWantToPlay()
    {
        return PctWon == null;
    }


    public String getId()
    {
        if (!StringUtils.isNullOrEmpty(ID))
        {
            return ID;
        }
        return GameID;
    }

    public String getTitle()
    {
        return Title;
    }

    public String getConsoleName()
    {
        return ConsoleName;
    }
}
