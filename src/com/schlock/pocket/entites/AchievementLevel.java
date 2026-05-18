package com.schlock.pocket.entites;

public enum AchievementLevel
{
    CURRENT,
    IN_PROGRESS,
    UNSTARTED,
    FINISHED,
    MASTERED;

    public boolean isNotCurrent()
    {
        return this != CURRENT;
    }

    public boolean isNotMastered()
    {
        return this != MASTERED;
    }
}
