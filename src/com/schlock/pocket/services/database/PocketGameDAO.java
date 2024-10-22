package com.schlock.pocket.services.database;

import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketGame;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class PocketGameDAO
{
    private final Session session;

    public PocketGameDAO(Session session)
    {
        this.session = session;
    }

    public List<PocketGame> getAll()
    {
        String text = "from PocketGame g order by g.id";
        Query query = session.createQuery(text);
        return query.list();
    }

    public List<PocketGame> getAllAvailableMister()
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.misterFilename is not null " +
                " and g.misterFilepath is not null ";

        Query query = session.createQuery(text);
        return query.list();
    }

    public PocketGame getByMisterFilename(String filename, PocketCore core)
    {
        String text = " select g " +
                " from PocketGame g " +
                " join g.core c " +
                " where g.misterFilename = :filename " +
                " and c.id = :coreId ";

        Query query = session.createQuery(text);
        query.setParameter("filename", filename);
        query.setParameter("coreId", core.getId());

        List<PocketGame> games = query.list();
        if (games.isEmpty())
        {
            return null;
        }
        return games.get(0);
    }

    public PocketGame getByPocketFilename(String filename)
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.pocketFilename = :filename ";

        Query query = session.createQuery(text);
        query.setParameter("filename", filename);

        List<PocketGame> games = query.list();
        if (games.isEmpty())
        {
            return null;
        }
        return games.get(0);
    }

    public List<PocketGame> getByPlatform(PlatformInfo platform)
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.platform = :platform ";

        Query query = session.createQuery(text);
        query.setParameter("platform", platform);

        return query.list();
    }

    public List<PocketGame> getByMissingBoxartThumbnail()
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.boxartConverted is false ";

        Query query = session.createQuery(text);

        List<PocketGame> games = query.list();
        return games;
    }

    public List<PocketGame> getByThumbnailCopiedNotInLibrary()
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.boxartConverted is true " +
                " and g.inLibrary is false ";

        Query query = session.createQuery(text);

        List<PocketGame> games = query.list();
        return games;
    }
}
