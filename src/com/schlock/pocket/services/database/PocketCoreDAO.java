package com.schlock.pocket.services.database;

import com.schlock.pocket.entites.PocketCore;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class PocketCoreDAO
{
    private final Session session;

    public PocketCoreDAO(Session session)
    {
        this.session = session;
    }

    public PocketCore getByNamespace(String namespace)
    {
        String text = " select c " +
                " from PocketCore c " +
                " where c.namespace = :namespace ";

        Query query = session.createQuery(text);
        query.setParameter("namespace", namespace);

        List<PocketCore> cores = query.list();

        if (cores.isEmpty())
        {
            return null;
        }
        return cores.get(0);
    }

    public List<PocketCore> getAllToCopyWithCompleteInformation()
    {
        String text = " select c " +
                " from PocketCore c " +
                " join c.category cat " +
                " where c.copy is true " +
                " and cat.copy is true " +
                " and c.name is not null " +
                " and c.namespace is not null " +
                " and c.category is not null " +
                " and c.manufacturer is not null " +
                " and c.year is not null ";

        Query query = session.createQuery(text);

        return query.list();
    }
}
