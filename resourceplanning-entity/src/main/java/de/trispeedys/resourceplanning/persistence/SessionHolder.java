package de.trispeedys.resourceplanning.persistence;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public class SessionHolder
{
    private SessionToken key;
    
    private Session session;

    public SessionHolder(SessionToken key, Session session)
    {
        super();
        this.key = key;
        this.session = session;
    }

    public Transaction beginTransaction()
    {
        return session.beginTransaction();
    }
    
    public Query createQuery(String queryString)
    {
        return session.createQuery(queryString);
    }

    public SessionToken getToken()
    {
        return key;
    }

    public void flush()
    {
        session.flush();
    }
    
    public Serializable saveOrUpdate(AbstractDbObject entity)
    {
        if (entity.isNew())
        {
            return session.save(entity);
        }
        session.update(entity);
        return null;
    }
}