package com.schlock.pocket.entites;

public enum AchievementLevel
{
    CURRENT,
    UNSTARTED,
    UNFINISHED,
    FINISHED,
    MASTERED;

    public String getName()
    {
        String name = name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        return name;
    }

    public boolean isNotCurrent()
    {
        return this != CURRENT;
    }

    public boolean isNotComplete()
    {
        return this != MASTERED && this != FINISHED;
    }
}
