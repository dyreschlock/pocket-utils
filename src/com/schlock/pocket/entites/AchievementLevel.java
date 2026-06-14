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
    SKIP(STARTED,"Skip"),

    UNSTARTED("Not Started"),
    UNSTARTED_RPG(UNSTARTED, "RPG"),
    UNSTARTED_PICROSS(UNSTARTED, "Picross"),

    OTHERS("others"),
    EVENT(OTHERS,"Event"),
    SUBSET(OTHERS,"Subset"),
    DONE(OTHERS,"Done");

    private final String SOFTCORE_FOLDER = "_Softcore";
    private final String TAPTO_FOLDER = ProcessMisterShortcuts.FAVORITES_FOLDER;


    private AchievementLevel parent;
    private String name;

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
        return getParent() != null && getParent().equals(OTHERS);
    }

    public boolean isCopyForTapTo()
    {
        return FOR_TAPTO.contains(this);
    }


    public String getAchievementFolderRelativeFilepath(String filename, boolean softcore)
    {
        String path;
        if (softcore)
        {
            path = "_" + OTHERS.name + "/" + SOFTCORE_FOLDER;
        }
        else if (this == CURRENT)
        {
            return filename;
        }
        else if (isOthers())
        {
            path = "_" + getParent().getName() + "/_." + getName();
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
        return "_" + OTHERS.getName() + "/" + TAPTO_FOLDER + "/" + filename;
    }
}
