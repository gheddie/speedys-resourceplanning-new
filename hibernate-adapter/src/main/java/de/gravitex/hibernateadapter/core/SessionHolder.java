package de.gravitex.hibernateadapter.core;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;

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
        datasource = new DefaultDatasource();
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
    public Object saveOrUpdate(DbObject entity)
    {
        return datasource.saveOrUpdate(sessionToken, entity);
    }
    
    @SuppressWarnings("unchecked")
    public void remove(DbObject entity)
    {
        datasource.remove(sessionToken, entity);
    }
}