package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;
import com.mysql.jdbc.StringUtils;

public class AchievementEntry
{
    private static final String BEATEN = "beaten-hardcore";
    private static final String MASTERED = "mastered";


    @Expose
    private String GameID;

    @Expose
    private String ID;

    @Expose
    private String Title;

    @Expose
    private String ConsoleName;

    @Expose
    private String HighestAwardKind;

    @Expose
    private Integer NumAwardedHardcore;

    public boolean isBeaten()
    {
        return BEATEN.equals(HighestAwardKind);
    }

    public boolean isMastered()
    {
        return MASTERED.equals(HighestAwardKind);
    }

    public boolean isHasProgress()
    {
        return NumAwardedHardcore != null && NumAwardedHardcore > 0;
    }

    public boolean isWantToPlay()
    {
        return NumAwardedHardcore == null;
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
