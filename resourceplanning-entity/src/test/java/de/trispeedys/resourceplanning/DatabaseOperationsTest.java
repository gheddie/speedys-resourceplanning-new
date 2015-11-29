package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningPersistenceException;

public class DatabaseOperationsTest
{
    @Test
    public void testFetchListByQuery()
    {
        // clear db
        HibernateUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.INACTIVE, 1, 1, 1980).saveOrUpdate();

        String qry =
                "FROM " +
                        Helper.class.getSimpleName() + " h WHERE h." + Helper.ATTR_HELPER_STATE +
                        " = :helperState";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        Datasources.getDatasource(Helper.class).find(qry, parameters);
    }

    @Test
    public void testFetchDedicated()
    {
        // clear db
        HibernateUtil.clearAll();

        Helper helper =
                EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();

        DefaultDatasource<Helper> datasource = Datasources.getDatasource(Helper.class);
        datasource.findById(helper.getId());
    }

    @Test
    public void testFetchListWithOneParamter()
    {
        // clear db
        HibernateUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();

        List<Helper> found =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        assertEquals(2, found.size());
    }

    @Test
    public void testFetchByClassWithMutlipleParamters()
    {
        // clear db
        HibernateUtil.clearAll();

        // create messages
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "SUB1",
                "BODY1", MessagingFormat.PLAIN).saveOrUpdate();
        EntityFactory.buildMessageQueue("klaus", "testhelper1.trispeedys@gmail.com", "SUB1", "BODY1",
                MessagingFormat.PLAIN).saveOrUpdate();
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "SUB1",
                "BODY2", MessagingFormat.PLAIN).saveOrUpdate();

        assertEquals(
                1,
                Datasources.getDatasource(MessageQueue.class)
                        .find(MessageQueue.ATTR_SUBJECT, "SUB1", MessageQueue.ATTR_BODY, "BODY1",
                                MessageQueue.ATTR_FROM_ADDRESS, "klaus")
                        .size());

        ;
    }

    // @Test(expected = ResourcePlanningPersistenceException.class)
    public void testValidationFail()
    {
        // clear db
        HibernateUtil.clearAll();

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null)
                .saveOrUpdate();
        EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null)
                .saveOrUpdate();
    }

    @Test
    public void testFindSome()
    {
        // clear db
        HibernateUtil.clearAll();
        // create positions
        for (int i = 1; i <= 10; i++)
        {
            EntityFactory.buildPosition("Pos" + i, i, SpeedyTestUtil.buildDefaultDomain(i), i, true)
                    .saveOrUpdate();
        }
        // fetch w/o parameters (all entries)
        assertEquals(10,
                Datasources.getDatasource(Position.class)
                        .find("FROM " + Position.class.getSimpleName())
                        .size());
        // fetch with class (all entries)
        assertEquals(10, Datasources.getDatasource(Position.class).findAll().size());
        // fetch with query string
        assertEquals(
                1,
                Datasources.getDatasource(Position.class)
                        .find("FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = 3")
                        .size());
        assertEquals(
                4,
                Datasources.getDatasource(Position.class)
                        .find("FROM " +
                                Position.class.getSimpleName() +
                                " pos WHERE pos.minimalAge >= 3 AND pos.minimalAge <= 6")
                        .size());
        // fetch with parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("minimalAge", 5);
        assertEquals(
                1,
                Datasources.getDatasource(Position.class)
                        .find("FROM " +
                                Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge",
                                parameters)
                        .size());
        assertEquals(
                1,
                Datasources.getDatasource(Position.class)
                        .find("FROM " +
                                Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge",
                                "minimalAge", 5)
                        .size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSingleResultFailing()
    {
        // clear db
        HibernateUtil.clearAll();

        // create helpers
        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();

        Datasources.getDatasource(Helper.class).findSingle(Helper.ATTR_LAST_NAME, "Helfer");
    }

    @Test
    public void testFindSingleResultOk()
    {
        // clear db
        HibernateUtil.clearAll();

        // create helpers
        EntityFactory.buildHelper("Hansen", "Klaus", "", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Meier", "Peter", "", HelperState.INACTIVE, 1, 1, 1980).saveOrUpdate();

        Helper activeHelper =
                Datasources.getDatasource(Helper.class).findSingle(Helper.ATTR_HELPER_STATE,
                        HelperState.ACTIVE);

        assertTrue(activeHelper != null);
    }

    // @Test(expected = ResourcePlanningPersistenceException.class)
    public void testInvalidParamterCount()
    {
        Datasources.getDatasource(Position.class).find("123", "456", "789");
    }
}