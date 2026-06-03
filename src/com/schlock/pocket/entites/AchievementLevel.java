package com.schlock.pocket.entites;

import com.schlock.pocket.app.ProcessMisterShortcuts;

import java.util.Arrays;
import java.util.List;

public enum AchievementLevel
{
    CURRENT(""),

    BEATEN("Beaten - Not Mastered"),
    MASTERED(BEATEN,"Mastered"),
    EASY_MASTER(BEATEN,"Easy or Near Master"),
    NEAR_MASTER(BEATEN,"Easy or Near Master"),

    STARTED("Not Beaten - Started"),

    UNSTARTED("Not Started"),
    UNSTARTED_RPG(UNSTARTED, "RPG"),

    EVENT("Event"),
    SUBSET("Subset"),

    SKIP("Skip"),
    DONE("Done");

    private AchievementLevel parent;
    private String name;

    private final static List<AchievementLevel> OTHER = Arrays.asList(DONE, EVENT, SKIP, SUBSET);
    private final static List<AchievementLevel> FOR_TAPTO = Arrays.asList(CURRENT, BEATEN, STARTED, UNSTARTED);

    AchievementLevel(String name)
    {
        this(null, name);
    }

    AchievementLevel(AchievementLevel parent, String name)
    {
        this.parent = parent;
        this.name = name;
    }

    public AchievementLevel getParent()
    {
        return parent;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean hasParent()
    {
        return parent != null;
    }

    public boolean isMastered()
    {
        return this == MASTERED;
    }

    public boolean isOthers()
    {
        return OTHER.contains(this);
    }

    public boolean isCopyForTapTo()
    {
        return FOR_TAPTO.contains(this);
    }

    public boolean isSubFolder()
    {
        return this.name.contains("/");
    }


    private final String OTHERS = "__others";
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
        else if (isOthers())
        {
            path = OTHERS + "/_." + getName();
        }
        else if(hasParent())
        {
            path = "_" + getParent().getName() + "/_" + getName();
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
