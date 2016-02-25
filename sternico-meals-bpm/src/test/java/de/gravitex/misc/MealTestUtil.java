package de.gravitex.misc;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.misc.entity.builder.MealDefinitionBuilder;
import de.gravitex.misc.entity.builder.MealRequesterBuilder;
import de.gravitex.misc.exception.MealRequestException;

public class MealTestUtil
{
    private static final Logger logger = Logger.getLogger(MealTestUtil.class);

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
            session.save(new MealRequesterBuilder().withFirstName("fn").withLastName("ln").withMailAddress("a1@b.de").build());
            session.save(new MealRequesterBuilder().withFirstName("fn").withLastName("ln").withMailAddress("a2@b.de").build());
            session.save(new MealRequesterBuilder().withFirstName("fn").withLastName("ln").withMailAddress("a3@b.de").build());
            tx.commit();
        }
        catch (Exception e)
        {
            logger.error("error --> rolling back : " + e.getMessage());
            tx.rollback();
            throw new MealRequestException("error on setting up requesters!", e);
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(session);
        }
    }

    public static void setupDefinitions()
    {
        Session session = SessionManager.getInstance().getSession(null);
        Transaction tx = null;
        try
        {
            tx = session.beginTransaction();
            session.save(new MealDefinitionBuilder().build());
            tx.commit();
        }
        catch (Exception e)
        {
            logger.error("error --> rolling back : " + e.getMessage());
            tx.rollback();
            throw new MealRequestException("error on setting meal definitions!", e);
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(session);
        }        
    }
}