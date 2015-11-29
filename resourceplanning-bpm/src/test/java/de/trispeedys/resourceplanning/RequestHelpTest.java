package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class RequestHelpTest
{
    private static final Helper DEFAULT_HELPER = EntityFactory.buildHelper("Stefan", "Schulz", "",
            HelperState.ACTIVE, 13, 2, 1976).saveOrUpdate();

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testParsingAndDeployment()
    {
        // ...
    }

    @Test
    public void testBusinessKeyGeneration()
    {
        assertEquals("bkRequestHelpHelperProcess_helper:123||event:456",
                ResourcePlanningUtil.generateRequestHelpBusinessKey(new Long(123), new Long(456)));
        assertEquals("bkRequestHelpHelperProcess_helper:10861||event:10862",
                ResourcePlanningUtil.generateRequestHelpBusinessKey(new Long(10861), new Long(10862)));
    }

    /**
     * trying to start process without passing a business key
     */
    @Test(expected = ResourcePlanningException.class)
    @Deployment(resources = "RequestHelp.bpmn")
    public void testStartWithoutBusinessKey()
    {
        // clear db
        HibernateUtil.clearAll();

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // ...
        Event event = EntityFactory.buildEvent("", "", 1, 1, 2000, EventState.PLANNED, template, null).saveOrUpdate();
        RequestHelpTestUtil.startHelperRequestProcess(DEFAULT_HELPER, event, null, rule);
    }

    /**
     * For a helper proposed for TRI 2016 without prior assignments, the user task for manual assignment must be
     * generated instantly.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFirstAssigmnment()
    {
        // clear all tables in db
        HibernateUtil.clearAll();

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // create event
        Event evt2016 =
                EntityFactory.buildEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016, EventState.PLANNED,
                        template, null).saveOrUpdate();
        // create helper
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE, 1, 1, 1990).saveOrUpdate();
        // start process
        RequestHelpTestUtil.startHelperRequestProcess(helper, evt2016,
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), evt2016.getId()), rule);
        // check
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(
                BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, rule));
    }

    /**
     * Follow up assignment -> assignment wished as before (2014), but position is already occupied in 2015 (to
     * 'blocking helper'). In this case, the user must get a mail which proposes other assignments to hin (and also
     * answer it).
     */
    // TODO fix test
    // @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssigmnmentForPosition()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create position
        Position positionBikeEntry =
                EntityFactory.buildPosition("Radeinfahrt Helmkontrolle", 12,
                        SpeedyTestUtil.buildDefaultDomain(1), 0, true).saveOrUpdate();
        // create events
        Event evt2014 =
                EntityFactory.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014, EventState.PLANNED, null, null)
                        .saveOrUpdate();
        Event evt2015 =
                EntityFactory.buildEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED, null, null)
                        .saveOrUpdate();
        // create helper
        Helper createdHelper =
                EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE, 1, 1, 1990).saveOrUpdate();
        Helper blockingHelper =
                EntityFactory.buildHelper("Blocking", "Helper", "", HelperState.ACTIVE, 1, 1, 1990).saveOrUpdate();
        // assign position to event
        SpeedyRoutines.relateEventsToPosition(positionBikeEntry, evt2014, evt2015);
        // assign helper to position in 2014
        EntityFactory.buildHelperAssignment(createdHelper, evt2014, positionBikeEntry).saveOrUpdate();
        // assign position to another helper in 2015
        EntityFactory.buildHelperAssignment(blockingHelper, evt2015, positionBikeEntry).saveOrUpdate();
        // start request process for 2015...
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(createdHelper.getId(), evt2015.getId());
        RequestHelpTestUtil.startHelperRequestProcess(createdHelper, evt2015, businessKey, rule);
        // correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK,
                businessKey, variables);
        // now the helper must choose a proposed position
        // as the desired position is blocked by 'blocking helper' in 2015...
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey,
                variables);
    }

    // TODO fix test
    // @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testStartReminderProcesses()
    {
        // clear all tables in db
        HibernateUtil.clearAll();

        // create event
        TestDataGenerator.createSimpleEvent("TRI", "TRI", 1, 1, 1980, EventState.FINISHED,
                EventTemplate.TEMPLATE_TRI);

        // there should be 5 active helpers...
        RequestHelpTestUtil.startTriggerHelperProcess(rule);

        // ...so 5 helper request process should have been started!!
        assertEquals(
                5,
                rule.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processDefinitionKey(RequestHelpTestUtil.PROCESS_DEFINITION_KEY_HELPER_PROCESS)
                        .list()
                        .size());
    }

    /**
     * create little event with helpers, duplicate it, make sure every helper gets a reminder mail for the following up
     * event.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingEventWithSameHelpers()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Event event2015 =
                TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED,
                        EventTemplate.TEMPLATE_TRI);
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "TRI-2016", "TRI-2016", 21, 6, 2015, null, null);
        // start request process for every helper
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find("helperState", HelperState.ACTIVE);
        String businessKey = null;
        for (Helper helper : activeHelpers)
        {
            businessKey =
                    ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, rule);
        }
        // a mail for every helper must have been sent
        assertEquals(activeHelpers.size(), Datasources.getDatasource(MessageQueue.class).findAll().size());
    }

    /**
     * same as {@link RequestHelpTest#testFollowingEventWithSameHelpers()} , but additional (new) helpers are available.
     * Everyone of those must get a manual assignment.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingEventWithOldAndNewHelpers()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 3 new helpers
        EntityFactory.buildHelper("Helper1", "Helper1", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Helper2", "Helper2", "a@b.de", HelperState.ACTIVE, 1, 2, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Helper3", "Helper3", "a@b.de", HelperState.ACTIVE, 1, 3, 1980).saveOrUpdate();
        // create 'little' event for 2015
        Event event2015 =
                TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED,
                        EventTemplate.TEMPLATE_TRI);
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "TRI-2016", "TRI-2016", 21, 6, 2015, null, null);
        // start request process for every helper
        List<Helper> helpers =
                Datasources.getDatasource(Helper.class).find("helperState", HelperState.ACTIVE);
        String businessKey = null;
        for (Helper helper : helpers)
        {
            businessKey =
                    ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, rule);
        }
        // a mail for every helper must have been sent
        assertEquals(5, Datasources.getDatasource(MessageQueue.class).findAll().size());
        // three manual assignment tasks must have been generated
        assertEquals(
                3,
                rule.getTaskService()
                        .createTaskQuery()
                        .taskDefinitionKey(
                                BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                        .list()
                        .size());
    }

    /**
     * Helper wants to be assigned on the same position as before, which is available, so he gets assigned to it by the
     * system without human interaction.
     */
    // @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testAutonomicBooking()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'minimal' event for 2015
        Event event2015 =
                TestDataGenerator.createMinimalEvent("TRI-2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "TRI-2016", "TRI-2016", 21, 6, 2015, null, null);
        // select created helper
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findAll().get(0);
        // start process
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, rule);
        // answer to mail
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK,
                businessKey, variables);
        // position the helper was assigned to in 2015
        Position priorPosition = HelperService.getHelperAssignment(helper, event2015);
        // helper should be (in 2016) booked to the same position as in 2015 now...
        assertEquals(true, HelperService.isHelperAssignedForPosition(helper, event2016, priorPosition));
    }
}