package de.trispeedys.resourceplanning;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.trispeedys.resourceplanning.persistence.SessionManager;

public class HibernateUtil
{
    private static final SessionFactory sessionFactory = buildSessionFactory();

    @SuppressWarnings("deprecation")
    private static SessionFactory buildSessionFactory()
    {
        try
        {
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex)
        {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public static void shutdown()
    {
        getSessionFactory().close();
    }

    public static void clearAll()
    {
        clearTable("template_domain");
        clearTable("domain_responsibility");
        clearTable("aggregation_relation");
        clearTable("position_aggregation");        
        clearTable("event_position");
        clearTable("helper_assignment");
        clearTable("position");
        clearTable("domain");
        clearTable("helper");
        clearTable("event");
        clearTable("event_template");
        clearTable("message_queue");
    }

    private static void clearTable(String tableName)
    {
        Session session = SessionManager.getInstance().getSession(null);
        Transaction tx = session.beginTransaction();
        String queryString = "delete from " + tableName;
        session.createSQLQuery(queryString).executeUpdate();
        tx.commit();
        SessionManager.getInstance().unregisterSession(session);
    }
}
