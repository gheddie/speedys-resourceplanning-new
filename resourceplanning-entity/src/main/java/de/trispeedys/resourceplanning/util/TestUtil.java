package de.trispeedys.resourceplanning.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.core.SessionManager;

public class TestUtil
{
    public static void clearAll()
    {
        clearTable("assignment_swap");
        clearTable("manual_assignment_comment");
        clearTable("template_domain");
        clearTable("domain_responsibility");
        clearTable("aggregation_relation");
        clearTable("position_aggregation");        
        clearTable("event_position");
        clearTable("helper_assignment");
        clearTable("missed_assignment");
        clearTable("position");
        clearTable("domain");
        clearTable("message_queue");
        clearTable("helper");
        clearTable("person");
        clearTable("guided_event");
        clearTable("simple_event");
        clearTable("event_template");
        clearTable("event_location");
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