package de.trispeedys.resourceplanning.persistence;

import java.util.HashMap;

import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;

public class SessionManager
{
    private HashMap<SessionToken, Session> sessions;
    
    private static SessionManager instance;
    
    private SessionManager()
    {
        sessions = new HashMap<SessionToken, Session>();
    }
    
    public SessionHolder registerSession(Object sessionHolder)
    {
        SessionToken token = generateSessionKey(sessionHolder);
        Session session = HibernateUtil.getSessionFactory().openSession();
        sessions.put(token, session);
        return new SessionHolder(token, session);       
    }
    
    public void unregisterSession(SessionHolder sessionHolder)
    {
        Session s = sessions.get(sessionHolder.getToken());
        if (s != null)
        {
            s.close();
        }
    }

    private SessionToken generateSessionKey(Object sessionHolder)
    {
        return new SessionToken(sessionHolder.getClass().getSimpleName() + "_" + System.currentTimeMillis());
    }

    public Session getSession(SessionToken sessionToken)
    {
        if (sessionToken == null)
        {
            // get a fresh session
            return HibernateUtil.getSessionFactory().openSession();   
        }               
        else
        {
            // get a cached session
            return sessions.get(sessionToken);
        }
    }
    
    public static SessionManager getInstance()
    {
        if (SessionManager.instance == null)
        {
            SessionManager.instance = new SessionManager();
        }
        return SessionManager.instance;
    }  
}