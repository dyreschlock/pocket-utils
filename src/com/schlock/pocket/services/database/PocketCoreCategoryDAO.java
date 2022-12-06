package com.schlock.pocket.services.database;

import com.schlock.pocket.entites.PocketCoreCategory;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class PocketCoreCategoryDAO
{
    private final Session session;

    public PocketCoreCategoryDAO(Session session)
    {
        this.session = session;
    }

    public PocketCoreCategory getByName(String name)
    {
        String text = " select c " +
                " from PocketCoreCategory c " +
                " where c.name = :name ";

        Query query = session.createQuery(text);
        query.setParameter("name", name);

        List<PocketCoreCategory> categories = query.list();
        if (categories.isEmpty())
        {
            return null;
        }
        return categories.get(0);
    }
}
