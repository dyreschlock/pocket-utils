package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;

public class AchievementEntry
{
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



    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

    public String getConsoleName()
    {
        return ConsoleName;
    }

    public void setConsoleName(String consoleName)
    {
        ConsoleName = consoleName;
    }

    public String getPctWon()
    {
        return PctWon;
    }

    public void setPctWon(String pctWon)
    {
        PctWon = pctWon;
    }

    public String getHardcoreMode()
    {
        return HardcoreMode;
    }

    public void setHardcoreMode(String hardcoreMode)
    {
        HardcoreMode = hardcoreMode;
    }
}
