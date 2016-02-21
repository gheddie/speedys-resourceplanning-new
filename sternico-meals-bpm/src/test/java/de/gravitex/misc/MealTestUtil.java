package de.gravitex.misc;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.misc.entity.MealRequester;
import de.gravitex.misc.entity.builder.MealRequesterBuilder;

public class MealTestUtil
{
    public static void clearAll()
    {
        clearTable("meal_proposal");
        clearTable("meal_definition");
        clearTable("meal_requester");
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

    public static void setupRequesters()
    {
        Session session = SessionManager.getInstance().getSession(null);
        Transaction tx = null;
        try
        {
            tx = session.beginTransaction();
            session.save(new MealRequesterBuilder().build());
            tx.commit();
        }
        catch (Exception e)
        {
            tx.rollback();
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(session);
        }
    }
}