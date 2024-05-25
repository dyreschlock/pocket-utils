package com.schlock.pocket.services.database;

import com.schlock.pocket.entites.PlaystationGame;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class PlaystationGameDAO
{
    private final Session session;

    public PlaystationGameDAO(Session session)
    {
        this.session = session;
    }

    public PlaystationGame getByGameId(String gameId)
    {
        String text = " select g " +
                " from PlaystationGame g " +
                " where g.gameId = :gameId ";

        Query query = session.createQuery(text);
        query.setParameter("gameId", gameId);

        List<PlaystationGame> games = query.list();
        if (games.isEmpty())
        {
            return null;
        }
        return games.get(0);
    }
}
