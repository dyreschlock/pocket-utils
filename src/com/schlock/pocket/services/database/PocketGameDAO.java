package com.schlock.pocket.services.database;

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

    public PocketGame getByFilename(String filename)
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.gameFilename = :filename ";

        Query query = session.createQuery(text);
        query.setParameter("filename", filename);

        List<PocketGame> games = query.list();

        if (games.isEmpty())
        {
            return null;
        }
        return games.get(0);
    }

    public List<PocketGame> getByLibraryThumbnailNotYetCreated()
    {
        String text = " select g " +
                " from PocketGame g " +
                " where g.imageCopied is false " +
                " or g.inLibrary is false ";

        Query query = session.createQuery(text);

        List<PocketGame> games = query.list();

        return games;
    }
}
