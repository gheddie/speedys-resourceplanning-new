package de.gravitex.misc;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.misc.entity.MealDefinition;
import de.gravitex.misc.entity.MealProposal;

public class DatabaseTest
{
    @Test
    public void testPersist()
    {
        MealTestUtil.clearAll();
        
        Transaction tx = null;
        Session session = SessionManager.getInstance().getSession(null);
        tx = session.beginTransaction();
        MealDefinition definition = new MealDefinition();
        session.save(definition);
        MealProposal request = new MealProposal();
        request.setMealDefinition(definition);
        session.save(request);
        tx.commit();
    }
}