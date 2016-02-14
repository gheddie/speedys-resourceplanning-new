package de.gravitex.hibernateadapter.core;

import java.math.BigInteger;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Interceptor;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import de.gravitex.hibernateadapter.core.util.HibernateUtil;

public class SessionManager
{
    private static final Logger logger = Logger.getLogger(SessionManager.class);
    
    private static final String SEQUENCE_NAME = "session_manager_sequence";

    private static final int MAX_OPEN_SESSIONS = 50;

    private HashMap<SessionToken, Session> sessions;
    
    private static SessionManager instance;
    
    private int openSessionCounter;
    
    private SessionManager()
    {
        sessions = new HashMap<SessionToken, Session>();
        openSessionCounter = 0;
    }
    
    public SessionHolder registerSession(Object sessionHolder, Interceptor interceptor)
    {
        SessionToken token = generateSessionKey(sessionHolder);
        Session session = getSession(null, interceptor);
        sessions.put(token, session);
        SessionHolder result = new SessionHolder(token, session);        
        return result;       
    }
    
    public void unregisterSession(Session session)
    {
        if (session != null)
        {
            session.close();
        }
        openSessionCounter--;
        logger.debug("SESSION CLOSED - open session count is now "+openSessionCounter+".");
    }
    
    public void unregisterSession(SessionHolder sessionHolder)
    {
        unregisterSession(sessions.get(sessionHolder.getToken()));
    }

    private SessionToken generateSessionKey(Object sessionHolder)
    {
        return new SessionToken(sessionHolder.getClass().getSimpleName() + "_" + readSequenceValue() + "_" + System.currentTimeMillis());
    }

    private Long readSequenceValue()
    {
        Session session = getSession(null);
        SQLQuery qry = session.createSQLQuery( "select nextval('"+SEQUENCE_NAME+"')" );
        long result = ((BigInteger) qry.uniqueResult()).longValue();
        unregisterSession(session);
        return result;
    }
    
    public Session getSession(SessionToken sessionToken)
    {
        return getSession(sessionToken, null);
    }

    public Session getSession(SessionToken sessionToken, Interceptor interceptor)
    {
        openSessionCounter++;

        if (openSessionCounter > MAX_OPEN_SESSIONS)
        {
            logger.debug("open session count (" + openSessionCounter + ") exceeded max value of '" + MAX_OPEN_SESSIONS + "'!!");
        }
        else
        {
            logger.debug("SESSION OPENED - open session count is now " + openSessionCounter + ".");
        }

        if (sessionToken == null)
        {
            // get me a fresh session (with or without an interceptor)
            return (interceptor != null
                    ? HibernateUtil.getSessionFactory().withOptions().interceptor(interceptor).openSession()
                    : HibernateUtil.getSessionFactory().openSession());
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

    public int getOpenSessionCount()
    {
        return openSessionCounter;
    }
}