package de.trispeedys.resourceplanning.persistence;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

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

    public Serializable save(AbstractDbObject entity)
    {
        return session.save(entity);
    }

    public SessionToken getToken()
    {
        return key;
    }

    public void flush()
    {
        session.flush();
    }
}