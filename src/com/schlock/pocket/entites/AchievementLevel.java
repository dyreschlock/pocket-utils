package com.schlock.pocket.entites;

import com.schlock.pocket.app.ProcessMisterShortcuts;

import java.util.Arrays;
import java.util.List;

public enum AchievementLevel
{
    CURRENT(""),
    IN_PROGRESS("In Progress"),

    UNFINISHED("Not Finished"),

    UNSTARTED("Not Started"),
    UNSTARTED_RPG("RPG"),

    EVENT("Event"),
    SUBSET("Subset"),

    FINISHED("Done"),
    MASTERED("Done");

    private String name;

    private final static List<AchievementLevel> ELEVATED = Arrays.asList(IN_PROGRESS, UNSTARTED, UNSTARTED_RPG, UNFINISHED);
    private final static List<AchievementLevel> FOR_TAPTO = Arrays.asList(CURRENT, IN_PROGRESS, UNFINISHED, UNSTARTED);

    AchievementLevel(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isMastered()
    {
        return this == MASTERED;
    }

    public boolean isElevated()
    {
        return ELEVATED.contains(this);
    }

    public boolean isCopyForTapTo()
    {
        return FOR_TAPTO.contains(this);
    }

    public boolean isUnstartedSub()
    {
        if (!UNSTARTED.equals(this))
        {
            String prefix = this.name().split("_")[0];
            return prefix.equalsIgnoreCase(UNSTARTED.name());
        }
        return false;
    }



    private final String OTHERS = "_others";
    private final String SOFTCORE = "_Softcore";
    private final String TAPTO = ProcessMisterShortcuts.FAVORITES_FOLDER;

    public String getAchievementFolderRelativeFilepath(String filename, boolean softcore)
    {
        String path;
        if (softcore)
        {
            path = OTHERS + "/" + SOFTCORE;
        }
        else if (this == CURRENT)
        {
            return filename;
        }
        else if (!isElevated())
        {
            path = OTHERS + "/_." + getName();
        }
        else if (isUnstartedSub())
        {
            path = "_" + UNSTARTED.getName() + "/_" + getName();
        }
        else
        {
            path = "_" + getName();
        }
        return path + "/" + filename;
    }

    public String getAchievementFolderTapToRelativeFilepath(String filename)
    {
        return OTHERS + "/" + TAPTO + "/" + filename;
    }
}
