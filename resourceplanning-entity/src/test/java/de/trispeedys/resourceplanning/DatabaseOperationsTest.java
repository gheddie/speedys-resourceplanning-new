package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.importer.JsonEventReader;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.TestUtil;

public class DatabaseOperationsTest
{

    @Test
    public void testSessionCount()
    {
        // clear db
        TestUtil.clearAll();

        // all session closed again in the beginning...
        assertEquals(0, SessionManager.getInstance().getOpenSessionCount());

        new JsonEventReader().doImport("Helfer_2015.json");

        // select some stuff
        RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();

        // ...and in the end.
        // assertEquals(0, SessionManager.getInstance().getOpenSessionCount());
    }

    @Test
    public void testFetchListByQuery()
    {
        // clear db
        TestUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.INACTIVE, 1, 1, 1980, true).saveOrUpdate();

        String qry = "FROM " + Helper.class.getSimpleName() + " h WHERE h." + Helper.ATTR_HELPER_STATE + " = :helperState";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        Datasources.getDatasource(Helper.class).find(null, qry, parameters);
    }

    @Test
    public void testFetchDedicated()
    {
        // clear db
        TestUtil.clearAll();

        Helper helper = EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();

        DefaultDatasource<Helper> datasource = Datasources.getDatasource(Helper.class);
        datasource.findById(null, helper.getId());
    }

    @Test
    public void testFetchListWithOneParamter()
    {
        // clear db
        TestUtil.clearAll();

        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();

        List<Helper> found = Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        assertEquals(2, found.size());
    }

    // @Test(expected = ResourcePlanningPersistenceException.class)
    public void testValidationFail()
    {
        // clear db
        TestUtil.clearAll();

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null).saveOrUpdate();
        EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null).saveOrUpdate();
    }

    @Test
    public void testFindSome()
    {
        // clear db
        TestUtil.clearAll();
        // create positions
        for (int i = 1; i <= 10; i++)
        {
            EntityFactory.buildPosition("Pos" + i, i, SpeedyTestUtil.buildDefaultDomain(i), i, true).saveOrUpdate();
        }
        // fetch w/o parameters (all entries)
        assertEquals(10, Datasources.getDatasource(Position.class).find(null, "FROM " + Position.class.getSimpleName()).size());
        // fetch with class (all entries)
        assertEquals(10, Datasources.getDatasource(Position.class).findAll(null).size());
        // fetch with query string
        assertEquals(1, Datasources.getDatasource(Position.class).find(null, "FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = 3").size());
        assertEquals(4, Datasources.getDatasource(Position.class).find(null, "FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge >= 3 AND pos.minimalAge <= 6").size());
        // fetch with parameters
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("minimalAge", 5);
        assertEquals(1, Datasources.getDatasource(Position.class).find(null, "FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge", parameters).size());
        assertEquals(1, Datasources.getDatasource(Position.class).find(null, "FROM " + Position.class.getSimpleName() + " pos WHERE pos.minimalAge = :minimalAge", "minimalAge", 5).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSingleResultFailing()
    {
        // clear db
        TestUtil.clearAll();

        // create helpers
        EntityFactory.buildHelper("Helfer", "Eins", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();
        EntityFactory.buildHelper("Helfer", "Zwei", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();

        Datasources.getDatasource(Helper.class).findSingle(null, Helper.ATTR_LAST_NAME, "Helfer");
    }

    @Test
    public void testFindSingleResultOk()
    {
        // clear db
        TestUtil.clearAll();

        // create helpers
        EntityFactory.buildHelper("Hansen", "Klaus", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();
        EntityFactory.buildHelper("Meier", "Peter", "", HelperState.INACTIVE, 1, 1, 1980, true).saveOrUpdate();

        Helper activeHelper = Datasources.getDatasource(Helper.class).findSingle(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);

        assertTrue(activeHelper != null);
    }

    // @Test(expected = ResourcePlanningPersistenceException.class)
    public void testInvalidParamterCount()
    {
        Datasources.getDatasource(Position.class).find(null, "123", "456", "789");
    }

    @Test
    public void testRemoveEntity()
    {
        // clear db
        TestUtil.clearAll();

        assertEquals(0, RepositoryProvider.getRepository(HelperRepository.class).findAll(null).size());

        // create a helper
        Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", "moo@foo.fi", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();

        assertEquals(1, RepositoryProvider.getRepository(HelperRepository.class).findAll(null).size());

        // remove the helper
        helper.remove();

        assertEquals(0, RepositoryProvider.getRepository(HelperRepository.class).findAll(null).size());
    }

    @Test
    public void testMissedAssignments()
    {
        // clear db
        TestUtil.clearAll();
        
        Event event = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6,
                        2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        
        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        List<Position> positions = RepositoryProvider.getRepository(PositionRepository.class).findAll();
        
        EntityFactory.buildMissedAssignment(event, helpers.get(0), positions.get(0)).saveOrUpdate();
        
        RepositoryProvider.getRepository(MissedAssignmentRepository.class).findUnusedByPositionAndEvent(positions.get(0).getId(), event.getId());
    }
}