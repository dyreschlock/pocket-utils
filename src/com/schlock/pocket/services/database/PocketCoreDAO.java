package com.schlock.pocket.services.database;

import com.schlock.pocket.entites.PocketCore;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Arrays;
import java.util.List;

public class PocketCoreDAO
{
    private final Session session;

    public PocketCoreDAO(Session session)
    {
        this.session = session;
    }

    public PocketCore getByPlatformId(String platformId)
    {
        String text = " select c " +
                " from PocketCore c " +
                " where c.platformId = :platformId ";

        Query query = session.createQuery(text);
        query.setParameter("platformId", platformId);

        List<PocketCore> cores = query.list();

        if (cores.isEmpty())
        {
            return null;
        }
        return cores.get(0);
    }

    public List<PocketCore> getAllToCopyReleaseWithCompleteInformation()
    {
        String text = " select c " +
                " from PocketCore c " +
                " join c.category cat " +
                " where cat.copy is true " +
                " and (c.copy is true or c.released is true) " +
                " and c.name is not null " +
                " and c.platformId is not null " +
                " and c.category is not null " +
                " and c.manufacturer is not null " +
                " and c.year is not null ";

        Query query = session.createQuery(text);
        return query.list();
    }

    public List<PocketCore> getAllToCopyWithCompleteInformation()
    {
        String text = " select c " +
                " from PocketCore c " +
                " join c.category cat " +
                " where c.copy is true " +
                " and cat.copy is true " +
                " and c.name is not null " +
                " and c.platformId is not null " +
                " and c.category is not null " +
                " and c.manufacturer is not null " +
                " and c.year is not null ";

        Query query = session.createQuery(text);

        return query.list();
    }

    public List<PocketCore> getAllToCopyWithCompleteInformationMister()
    {
        String text = " select c " +
                " from PocketCore c " +
                " join c.category cat " +
                " where c.copy is true " +
                " and cat.copy is true " +
                " and c.name is not null " +
                " and c.platformId is not null " +
                " and c.category is not null " +
                " and c.manufacturer is not null " +
                " and c.year is not null " +
                " and c.misterId is not null ";

        Query query = session.createQuery(text);
        return query.list();
    }

    public List<PocketCore> getByCatCopyAndCatName(String catName, boolean released)
    {
        return getByCatCopyAndCatName(Arrays.asList(catName), released);
    }

    public List<PocketCore> getByCatCopyAndCatName(List<String> catName, boolean released)
    {
        String text = " select c from PocketCore c " +
                " join c.category cat " +
                " where cat.copy is true " +
                " and cat.name in (:names) ";

        if(released)
        {
            text += " and c.released is true ";
        }
        else
        {
            text += " and c.released is false ";
        }

        Query query = session.createQuery(text);
        query.setParameterList("names", catName);

        return query.list();
    }
}
