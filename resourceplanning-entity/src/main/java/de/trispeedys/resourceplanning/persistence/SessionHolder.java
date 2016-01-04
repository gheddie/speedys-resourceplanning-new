package de.trispeedys.resourceplanning.persistence;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public class SessionHolder
{
    private SessionToken sessionToken;
    
    private Session session;

    private Transaction tx;
    
    private DefaultDatasource datasource;

    public SessionHolder(SessionToken key, Session session)
    {
        super();
        this.sessionToken = key;
        this.session = session;
        datasource = Datasources.getDatasource(null);
    }

    public void beginTransaction()
    {
        tx = session.beginTransaction();
    }
    
    public void commitTransaction()
    {
        tx.commit();
    }
    
    public void rollbackTransaction()
    {
        tx.rollback();
    }
    
    public Query createQuery(String queryString)
    {
        return session.createQuery(queryString);
    }

    public SessionToken getToken()
    {
        return sessionToken;
    }

    public void flush()
    {
        session.flush();
    }
    
    @SuppressWarnings("unchecked")
    public Object saveOrUpdate(AbstractDbObject entity)
    {
        return datasource.saveOrUpdate(sessionToken, entity);
    }
    
    @SuppressWarnings("unchecked")
    public void remove(AbstractDbObject entity)
    {
        datasource.remove(sessionToken, entity);
    }
}