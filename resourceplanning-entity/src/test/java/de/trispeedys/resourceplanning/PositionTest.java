package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class PositionTest
{
    private static final Integer PRIO1 = new Integer(1);

    private static final Integer PRIO2 = new Integer(2);

    private static final Integer PRIO3 = new Integer(3);

    @Test
    public void testEventPositions()
    {
        TestUtil.clearAll();

        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // some events
        GuidedEvent event1 =
                EntityFactory.buildEvent("DM AK 2014", "DM-AK-2014", 21, 6, 2014, EventState.FINISHED, template, null)
                        .saveOrUpdate();
        GuidedEvent event2 =
                EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015, EventState.PLANNED, template, null)
                        .saveOrUpdate();

        // some positions
        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain(1);
        Position position1 = EntityFactory.buildPosition("Radverpflegung", 12, defaultDomain, 0, true).saveOrUpdate();
        Position position3 =
                EntityFactory.buildPosition("Irgendwas kontrollieren", 12, defaultDomain, 1, true).saveOrUpdate();
        Position position4 = EntityFactory.buildPosition("Gut aussehen", 12, defaultDomain, 2, true).saveOrUpdate();

        // some links between event 1 and positions
        EntityFactory.buildEventPosition(event1, position1).saveOrUpdate();
        EntityFactory.buildEventPosition(event1, position3).saveOrUpdate();
        EntityFactory.buildEventPosition(event1, position4).saveOrUpdate();

        // some links between event 2 and positions
        EntityFactory.buildEventPosition(event2, position1).saveOrUpdate();
        EntityFactory.buildEventPosition(event2, position3).saveOrUpdate();

        // event 1 should have 3 positions
        assertEquals(3, positionRepository.findPositionsInEvent(event1).size());

        // event 2 should have 2 positions
        assertEquals(2, positionRepository.findPositionsInEvent(event2).size());
    }

    @Test
    public void testPositionAggregationWithoutGroups()
    {
        TestUtil.clearAll();

        GuidedEvent event2016 =
                TestDataGenerator.createAggregationEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI, true);

        HashMap<Integer, Position> posMap = new HashMap<Integer, Position>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findAll(null))
        {
            posMap.put(pos.getPositionNumber(), pos);
        }

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll(null);
        // we assign one unpriorited...
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(0), event2016,
                posMap.get(0), null);
        // ...and two prio 1 tasks...
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(1), event2016,
                posMap.get(6), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(2), event2016,
                posMap.get(7), null);
        // ...and one prio 2 task...
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(3), event2016,
                posMap.get(11), null);

        // generator must provide 3 unpriorized and 3 PRIO 2 tasks...
        assertEquals(
                6,
                RepositoryProvider.getRepository(PositionRepository.class)
                        .findUnassignedPositionsByGenerator(helpers.get(0), event2016)
                        .size());
    }

    /**
     * Test priorization, but without any priorization provided on positions
     */
    // @Test
    // TODO fails after removing MessageQueue.messagingFormat
    public void testPositionAggregationWithoutGroupsNoPrios()
    {
        TestUtil.clearAll();

        GuidedEvent event2016 =
                TestDataGenerator.createAggregationEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI, false);

        HashMap<Integer, Position> posMap = new HashMap<Integer, Position>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findAll(null))
        {
            posMap.put(pos.getPositionNumber(), pos);
        }

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll(null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(0), event2016,
                posMap.get(0), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(1), event2016,
                posMap.get(6), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(2), event2016,
                posMap.get(7), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(3), event2016,
                posMap.get(11), null);

        // this must give 13-4=9 psotions to choose...
        assertEquals(
                9,
                RepositoryProvider.getRepository(PositionRepository.class)
                        .findUnassignedPositionsByGenerator(helpers.get(0), event2016)
                        .size());
    }

    /**
     * Test priorization with all unpriorized positions already given away...
     */
    // @Test
    // TODO fails after removing MessageQueue.messagingFormat
    public void testPositionAggregationWithAllUnpriorizedPositionsAssigned()
    {
        TestUtil.clearAll();

        GuidedEvent event2016 =
                TestDataGenerator.createAggregationEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI, true);

        HashMap<Integer, Position> posMap = new HashMap<Integer, Position>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findAll(null))
        {
            posMap.put(pos.getPositionNumber(), pos);
        }

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll(null);
        // we assign all unpriorited positions...
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(0), event2016,
                posMap.get(0), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(1), event2016,
                posMap.get(1), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(2), event2016,
                posMap.get(2), null);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(helpers.get(3), event2016,
                posMap.get(3), null);

        // this must give all prio 2 positions (4)...
        assertEquals(
                4,
                RepositoryProvider.getRepository(PositionRepository.class)
                        .findUnassignedPositionsByGenerator(helpers.get(0), event2016)
                        .size());
    }

    /**
     * Test choosable position generation with using {@link PositionAggregation} groups.
     */
    @Test
    public void testPositionAggregationWithRelationGroups()
    {
        TestUtil.clearAll();

        Helper helper = EntityFactory.buildHelper("M�ller", "Peter", "", HelperState.ACTIVE, 1, 1, 1980, true).saveOrUpdate();

        EventTemplate template = EntityFactory.buildEventTemplate("999").saveOrUpdate();

        GuidedEvent event = EntityFactory.buildEvent("TRI123", "TRI123", 14, 4, 2016, EventState.PLANNED, template, null).saveOrUpdate();

        Domain domain = EntityFactory.buildDomain("123", 123).saveOrUpdate();

        Position posA1 = EntityFactory.buildPosition("posA1", 12, domain, 1, true).saveOrUpdate();
        Position posA2 = EntityFactory.buildPosition("posA2", 12, domain, 2, true).saveOrUpdate();
        Position posA3 = EntityFactory.buildPosition("posA3", 12, domain, 3, true).saveOrUpdate();
        Position posA4 = EntityFactory.buildPosition("posA4", 12, domain, 4, true, PRIO1).saveOrUpdate();

        Position posB1 = EntityFactory.buildPosition("posB1", 12, domain, 5, true, PRIO2).saveOrUpdate();
        Position posB2 = EntityFactory.buildPosition("posB2", 12, domain, 6, true, PRIO1).saveOrUpdate();
        Position posB3 = EntityFactory.buildPosition("posB3", 12, domain, 7, true, PRIO3).saveOrUpdate();
        Position posB4 = EntityFactory.buildPosition("posB4", 12, domain, 8, true).saveOrUpdate();
        Position posB5 = EntityFactory.buildPosition("posB5", 12, domain, 9, true, PRIO1).saveOrUpdate();

        SpeedyRoutines.relatePositionsToEvent(event, posA1, posA2, posA3, posA4, posB1, posB2, posB3, posB4, posB5);

        SpeedyRoutines.createPositionAggregation("group1", posA4, posB1, posB2);

        SpeedyRoutines.createPositionAggregation("group2", posA2, posB3, posB4);

        int expectedNoGroup = 3;

        int expectedGroup1 = 1;

        int expectedGroup2 = 3;

        // TODO
        assertEquals(
                (expectedNoGroup + expectedGroup1 + expectedGroup2),
                RepositoryProvider.getRepository(PositionRepository.class)
                        .findUnassignedPositionsByGenerator(helper, event)
                        .size());
    }

    /**
     * A {@link Position} must only be in no or one {@link PositionAggregation} group.
     */
    @Test
    public void testPositionAggregationDisjunct()
    {
        TestUtil.clearAll();

        // TODO
    }

    @Test
    public void testNonEventPositions()
    {
        TestUtil.clearAll();

        GuidedEvent event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6,
                        2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016", "TRI-2016", 21, 6,
                        2016, null, null);
        
        PositionRepository repository = RepositoryProvider.getRepository(PositionRepository.class);
        
        assertEquals(0, repository.findEventPositions(event2016, false).size());
        
        Domain domain = RepositoryProvider.getRepository(DomainRepository.class).findAll(null).get(0);
        
        // create two new positions
        repository.createPosition("POS1", 111, domain, 12, true).saveOrUpdate();
        repository.createPosition("POS2", 222, domain, 12, true).saveOrUpdate();
        
        assertEquals(2, repository.findEventPositions(event2016, false).size());
    }
    
    @Test
    public void testMissedAssignmentExclusive()
    {
        TestUtil.clearAll();

        GuidedEvent event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6,
                        2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016", "TRI-2016", 21, 6,
                        2016, null, null);
        
        Position position1 = RepositoryProvider.getRepository(PositionRepository.class).findAll(null).get(0);
        Position position2 = RepositoryProvider.getRepository(PositionRepository.class).findAll(null).get(1);
        
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(0);
        
        MissedAssignmentRepository repository = RepositoryProvider.getRepository(MissedAssignmentRepository.class);
        
        // create miss
        repository.createMissedAssignmentExclusive(event2016.getId(), helper.getId(), position1.getId());
        
        // now, there is one missed assignment...
        assertEquals(1, repository.findAll().size());
        
        // create another miss for the same helper and the same event
        repository.createMissedAssignmentExclusive(event2016.getId(), helper.getId(), position2.getId());
        
        // now, there is also only one missed assignment as the prior must have been deleted...
        assertEquals(1, repository.findAll().size());
    }
}