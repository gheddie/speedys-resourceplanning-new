package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmJobDefinitions;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.rule.CallbackChoiceGenerator;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class RequestHelpExecutionTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    /**
     * - (0) clear database - (1) prepare event with helpers assigned for 2015 (all positions are assigned, same count
     * of positions and helpers) - (2) duplicate event (for 2016) - (3) assign the position which was assigned to helper
     * 'A' in 2015 to helper 'B' in 2016 - (4) start process for helper 'A' and event in 2016 who was assigned to that
     * position in the year before (2015) - (5) let helper 'A' state (after ingoring the first mail -->
     * 'REMINDER_STEP_0') he wants to be assigned as before (by sending message 'MSG_HELP_CALLBACK' with paramter
     * {@link HelperCallback#ASSIGNMENT_AS_BEFORE}) - (6) check availability must fail as the position is already
     * assigned (to 'B') - (7) mail with other positions (4) must be generated and sent to 'A' - (8) helper 'A' chooses
     * one of that positions ('C') (by triggering message 'MSG_POS_CHOSEN' with position id as variable) - (9)
     * confirmation mail must have been sent - (10) chosen position ('C') must be assigned to the helper 'A' afterwards
     * - (11) process must be finished (after signal for the event start)
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testProposePositionsOnAssignmentRequestedAsBefore()
    {
        // (0)
        HibernateUtil.clearAll();

        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);

        // (1)
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        // (2)
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // (3)
        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        // get assigned position for helper 'A' in 2015
        HelperAssignment assignmentA2015 = RepositoryProvider.getRepository(HelperAssignmentRepository.class).getHelperAssignments(helperA, event2015).get(0);
        AssignmentService.assignHelper(helperB, event2016, assignmentA2015.getPosition());

        // (4)
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        // (5)
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);

        // (6) --> if CheckAvailabiliy-Delegate fails, variable 'posAvailable' must be set to 'false'
        // ??? how to check the variable ???

        // (7) --> mails with types 'MessagingType.REMINDER_STEP_0' and 'MessagingType.PROPOSE_POSITIONS' must be
// there...
        List<Position> unassignedPositionsIn2016 = positionRepository.findUnassignedPositionsInEvent(event2016, false);
        assertEquals(4, unassignedPositionsIn2016.size());
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.REMINDER_STEP_0, MessagingType.REMINDER_STEP_1, MessagingType.PROPOSE_POSITIONS));

        // (8)
        Position chosenPosition = unassignedPositionsIn2016.get(1);
        RequestHelpTestUtil.choosePosition(businessKey, chosenPosition, processEngine, event2016.getId());

        // (9)
        assertTrue(RequestHelpTestUtil.checkMails(4, MessagingType.REMINDER_STEP_0, MessagingType.REMINDER_STEP_1, MessagingType.PROPOSE_POSITIONS,
                MessagingType.BOOKING_CONFIRMATION));

        // (10)
        // ...
        List<HelperAssignment> helperAssignmentA2016 =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());

        // (11)
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // assignment 'A' must have status 'CONFIRMED'
        assertEquals(HelperAssignmentState.CONFIRMED,
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperA, event2016).getHelperAssignmentState());
    }

    /**
     * Like {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore()}, but the position
     * helper ('A') was assigned to last year before is available (not blocked by helper 'B'). This means that 'A' must
     * be automatically assigned to his used position by the system.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testAssignmentRequestedAsBeforeSuccesful()
    {
        HibernateUtil.clearAll();

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> allHelpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);

        List<HelperAssignment> helperAssignmentA2016 =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());

        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }

    /**
     * tests the deactivation of a helper not respondig to any reminder mail, but in the end, he responds positive to
     * the 'last chance' mail (which means that he remains {@link HelperState#ACTIVE}).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNotCooperativeAndRecoveredHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Event event2015 = TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "TRI-2016", "TRI-2016", 21, 6, 2015, null, null);
        // start request process for every helper
        List<Helper> activeHelpers = Datasources.getDatasource(Helper.class).find("helperState", HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(), event2016.getId());

        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey, processEngine);

        // answer to mail (i do not want to be deactivated)
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_DEACT_RESP, businessKey);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // helper state remains 'ACTIVE'
        assertEquals(HelperState.ACTIVE, (RepositoryProvider.getRepository(HelperRepository.class).findById(notCooperativeHelper.getId())).getHelperState());
    }

    /**
     * Like {@link RequestHelpExecutionTest#testNotCooperativeAndRecoveredHelper()}, but the helper does NOT respond to
     * the last chance mail so that the one month timer is fired. That means that the helper gets deactived.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testCompletetlyUncooperativeHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Event event2015 = TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "TRI-2016", "TRI-2016", 21, 6, 2015, null, null);
        // start request process for every helper
        List<Helper> activeHelpers = Datasources.getDatasource(Helper.class).find("helperState", HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(), event2016.getId());

        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey, processEngine);

        // fire the 'last chance' timer
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_LAST_CHANCE_TIMER, processEngine);

        assertTrue(RequestHelpTestUtil.checkMails(5, MessagingType.REMINDER_STEP_0, MessagingType.REMINDER_STEP_1, MessagingType.REMINDER_STEP_2,
                MessagingType.DEACTIVATION_REQUEST, MessagingType.ALERT_HELPER_DEACTIVATED));

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // helper state changes to 'INACTIVE'
        assertEquals(HelperState.INACTIVE, ((Helper) Datasources.getDatasource(Helper.class).findById(notCooperativeHelper.getId())).getHelperState());
    }

    /**
     * Tests {@link HelperCallback#PAUSE_ME}.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPauseMe()
    {
        // clear all tables in db
        HibernateUtil.clearAll();

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variablesCallback);

        // process must be gone, helper must remain at state active
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
        assertEquals(HelperState.ACTIVE, ((Helper) Datasources.getDatasource(Helper.class).findById(helperA.getId())).getHelperState());

        // there must be a 'sorry to see you go' mail...
        assertTrue(RequestHelpTestUtil.checkMails(2, MessagingType.REMINDER_STEP_0, MessagingType.PAUSE_CONFIRM));
    }

    /**
     * Testing a process fpr a helper who wants to change his position (start like
     * {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore}, but the helper commits the
     * message for {@link HelperCallback#CHANGE_POS}) (A). He then chooses a positions which already has been assigned
     * to someone else (B), so he gets a second mail, choose a free position this time (C). Then the position is
     * assigned to 'helperA' and the process is gone after signal (D).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testChangePositions()
    {
        HibernateUtil.clearAll();

        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        // (A)
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKey, processEngine);

        // check mails ('REMINDER_STEP_0' und 'PROPOSE_POSITIONS' must be there)
        assertTrue(RequestHelpTestUtil.checkMails(2, MessagingType.REMINDER_STEP_0, MessagingType.PROPOSE_POSITIONS));

        // (B) we assign one of the proposed prosition to another helper and let 'helperA' choose it...
        Position blockedPosition = positionRepository.findUnassignedPositionsInEvent(event2016, false).get(0);
        Position notBlockedPosition = positionRepository.findUnassignedPositionsInEvent(event2016, false).get(1);
        AssignmentService.assignHelper(helperB, event2016, blockedPosition);
        RequestHelpTestUtil.choosePosition(businessKey, blockedPosition, processEngine, event2016.getId());

        // (C) --> there must be a second proposal mail
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.REMINDER_STEP_0, MessagingType.PROPOSE_POSITIONS));
        RequestHelpTestUtil.choosePosition(businessKey, notBlockedPosition, processEngine, event2016.getId());

        // (D)
        assertEquals(1, RepositoryProvider.getRepository(HelperAssignmentRepository.class).getHelperAssignments(helperA, event2016).size());
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // assignment 'A' must have status 'CONFIRMED'
        assertEquals(HelperAssignmentState.CONFIRMED,
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperA, event2016).getHelperAssignmentState());
    }

    /**
     * Helper 'A' chooses {@link HelperCallback#CHANGE_POS} and gets the correlating mail. Before choosing a position,
     * it gets blocked by helper 'B' (running the same process), so 'A' gets another mail with proposed positions.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPositionBlockedOnChoosePosition()
    {
        HibernateUtil.clearAll();

        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = Datasources.getDatasource(Helper.class).findAll();

        Helper helperA = helpers.get(0);
        String businessKeyA = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKeyA, processEngine);
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKeyA, processEngine);

        Helper helperB = helpers.get(2);
        String businessKeyB = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperB.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperB, event2016, businessKeyB, processEngine);
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKeyB, processEngine);

        List<Position> allUnassignedPositions = positionRepository.findUnassignedPositionsInEvent(event2016, false);
        Position desiredPosition = allUnassignedPositions.get(2);

        // 'B' is faster...
        RequestHelpTestUtil.choosePosition(businessKeyB, desiredPosition, processEngine, event2016.getId());

        // 'B' is booked for desired position...
        assertEquals(
                1,
                Datasources.getDatasource(HelperAssignment.class)
                        .find(HelperAssignment.ATTR_EVENT, event2016, HelperAssignment.ATTR_HELPER, helperB, HelperAssignment.ATTR_POSITION, desiredPosition)
                        .size());

        // 'A' comes to late
        RequestHelpTestUtil.choosePosition(businessKeyA, desiredPosition, processEngine, event2016.getId());

        // 'A' has two mails of type 'PROPOSE_POSITIONS'...
        assertEquals(
                2,
                Datasources.getDatasource(MessageQueue.class)
                        .find(MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.PROPOSE_POSITIONS, MessageQueue.ATTR_TO_ADDRESS, helperA.getEmail())
                        .size());
    }

    /**
     * A new helper is manually assigned, but after being assigned to a position, he cancels his assignment. That for,
     * an alert mail must be sent ({@link AppConfigurationValues#ADMIN_MAIL}) and the assignment must have the status
     * {@link HelperAssignmentState#CANCELLED}.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNewHelperCancelsAssignment()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // there must be 5 unassigned positions
        assertEquals(TestDataGenerator.POS_COUNT_SIMPLE_EVENT,
                RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false).size());

        // new helper
        Helper helper = EntityFactory.buildHelper("Mee", "Moo", "a@b.de", HelperState.ACTIVE, 23, 6, 2000).saveOrUpdate();

        // start process
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);

        // manual assignment task must be there
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));

        // finish it (with a position)
        Task task =
                processEngine.getTaskService()
                        .createTaskQuery()
                        .taskDefinitionKey(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                        .list()
                        .get(0);
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Position someUnassignedTask = (Position) Datasources.getDatasource(Position.class).findAll().get(0);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, someUnassignedTask.getId());
        processEngine.getTaskService().complete(task.getId(), variables);

        // there must be 4 unassigned positions
        assertEquals(TestDataGenerator.POS_COUNT_SIMPLE_EVENT - 1,
                RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false).size());

        // Send cancellation message
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, businessKey);

        // there must be 5 unassigned positions
        assertEquals(TestDataGenerator.POS_COUNT_SIMPLE_EVENT,
                RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false).size());

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // check canncelled assignment
        assertEquals(
                HelperAssignmentState.CANCELLED,
                ((HelperAssignment) Datasources.getDatasource(HelperAssignment.class)
                        .find(HelperAssignment.ATTR_EVENT, event2016, HelperAssignment.ATTR_HELPER, helper)
                        .get(0)).getHelperAssignmentState());

        // admin mail and confirmation to user must have been sent
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.BOOKING_CONFIRMATION, MessagingType.ALERT_BOOKING_CANCELLED,
                MessagingType.CANCELLATION_CONFIRM));
    }

    /**
     * A helper was assigned to two positions in the year before, and one of the positions is gone...does he get
     * {@link HelperCallback#ASSIGNMENT_AS_BEFORE}?
     */
    // @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testCallbackOptionsWithMultiplePriorAssignments()
    {
        assertTrue(1 == 2);
    }

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNoPositionsProposableManualAssignment()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // one of the helpers...
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);

        // start the process
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);

        // there must be 5 unassigned positions
        List<Position> unassignedPositions = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false);
        assertEquals(TestDataGenerator.POS_COUNT_SIMPLE_EVENT, unassignedPositions.size());

        // block all of them with fresh helpers...
        Helper new1 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).saveOrUpdate();
        AssignmentService.assignHelper(new1, event2016, unassignedPositions.get(0));
        Helper new2 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 3, 1976).saveOrUpdate();
        AssignmentService.assignHelper(new2, event2016, unassignedPositions.get(1));
        Helper new3 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 4, 1976).saveOrUpdate();
        AssignmentService.assignHelper(new3, event2016, unassignedPositions.get(2));
        Helper new4 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 5, 1976).saveOrUpdate();
        AssignmentService.assignHelper(new4, event2016, unassignedPositions.get(3));
        Helper new5 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 6, 1976).saveOrUpdate();
        AssignmentService.assignHelper(new5, event2016, unassignedPositions.get(4));

        // there must be 0 unassigned positions
        assertEquals(0, RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false).size());

        // choose 'CHANGE_POS'
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKey, processEngine);

        // manual assignment task must be there...
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));
    }

    /**
     * A helper (who has already been assigned before) without an email address must be manually assigned...
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNoMailRequiresManualAssignment()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // one of the helpers...
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);

        // set email address to null...
        helper.setEmail(null);
        RepositoryProvider.getRepository(HelperRepository.class).saveOrUpdate(helper);

        // reload helper without mail
        Helper reloadedHelper = RepositoryProvider.getRepository(HelperRepository.class).findById(helper.getId());

        // start the process
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(reloadedHelper, event2016, businessKey, processEngine);

        // manual assignment task must be there...
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));
    }

    /**
     * Helper chooses {@link HelperCallback#ASSIGN_ME_MANUALLY}...
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssignmentWithManualAssignmentChosen()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // one of the helpers...
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);

        // start the process
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);

        // choose manual assignment
        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGN_ME_MANUALLY, businessKey, processEngine);

        // manual assignment task must be there...
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));
    }

    /**
     * Helper 'A' gets callback mail (reminder step 0) with all options and does not respond for a week. In the
     * meantime, has former assignment is given away to 'B'. Then, his second mail (reminder step 1) must only have 3
     * options ('ASSIGNMENT_AS_BEFORE' is gone).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReducedOptionsAfterReminderStepOne()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // the helpers...
        List<Helper> allHelpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        Helper helperA = allHelpers.get(0);
        Helper helperB = allHelpers.get(1);

        String businessKeyA = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKeyA, processEngine);

        String businessKeyB = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperB.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperB, event2016, businessKeyB, processEngine);

        // 'B' gets prior assignment of 'A'
        HelperAssignment priorAssignmentA = AssignmentService.getPriorAssignment(helperA, event2016.getEventTemplate());
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKeyB, processEngine);
        Long chosenPositionId = priorAssignmentA.getPosition().getId();
        boolean positionAvailable = PositionService.isPositionAvailable(event2016.getId(), chosenPositionId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKeyB, variables);

        // here comes reminder step 1 for 'A'
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);

        // check choices
        assertTrue(CallbackChoiceGeneratorTest.checkChoices(new HelperCallback[]
        {
                HelperCallback.ASSIGN_ME_MANUALLY, HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME
        }, new CallbackChoiceGenerator().generate(helperA, event2016)));
    }

    // @Test TODO
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPendingProcessInstances()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // start all the process
        String businessKey = null;
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        for (Helper helper : activeHelpers)
        {
            businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);
        }

        // 5 executions
        assertEquals(5, processEngine.getRuntimeService().createExecutionQuery().processDefinitionKey("RequestHelpHelperProcess").list().size());
    }

    /**
     * If a helper chooses a position which is already blocked then
     * {@link BpmVariables.RequestHelpHelper#VAR_POS_CHOOSING_REENTRANT} must be set so that he gets a hint in the
     * following proposal mail which tells him why he got it.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testChoosePositionsReentrancy()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // start all the process
        String businessKey = null;
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        List<ProcessInstance> executions = new ArrayList<ProcessInstance>();
        for (Helper helper : activeHelpers)
        {
            businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            executions.add(RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine));
        }

        ProcessInstance executionA = executions.get(0);
        ProcessInstance executionB = executions.get(1);

        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, executionA.getBusinessKey(), processEngine);
        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, executionB.getBusinessKey(), processEngine);

        // find out assigned position (should be one)
        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findAllHelperAssignmentsByEvent(event2016);
        assertEquals(1, assignments.size());
        Position pos = assignments.get(0).getPosition();

        // let execution 'A' choose the position...
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, pos.getId());
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, PositionService.isPositionAvailable(event2016.getId(), pos.getId()));
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, executionA.getBusinessKey(), variables);

        // now, reentrancy flag should be set in execution 'A'...
        List<VariableInstance> fetchedVariables =
                processEngine.getRuntimeService()
                        .createVariableInstanceQuery()
                        .variableName(BpmVariables.RequestHelpHelper.VAR_POS_CHOOSING_REENTRANT)
                        .executionIdIn(executionA.getId())
                        .list();
        assertEquals(1, fetchedVariables.size());

        // it should be 'true'...
        assertTrue((Boolean) fetchedVariables.get(0).getValue());
    }

    /**
     * A helper wants to choose {@link HelperCallback#ASSIGNMENT_AS_BEFORE}, but all positions are assigned to new
     * helpers. This must lead to manual assignment for the helper.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testAssignmentAsBeforeWithAllPositionsBlocked()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);

        // start all the process
        String businessKey = null;
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        List<ProcessInstance> executions = new ArrayList<ProcessInstance>();
        for (Helper helper : activeHelpers)
        {
            businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            executions.add(RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine));
        }

        // pick a helper
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);

        // block positions
        List<Position> unassigned = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false);
        assertEquals(5, unassigned.size());
        Helper newHelper = null;
        int i = 0;
        for (Position posUnassigned : unassigned)
        {
            newHelper = EntityFactory.buildHelper("AAA", "BBB", "a@b.de", HelperState.ACTIVE, 1, 1, 1970 + i).saveOrUpdate();
            i++;
            AssignmentService.assignHelper(newHelper, event2016, posUnassigned);
        }
        unassigned = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, false);
        assertEquals(0, unassigned.size());

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE,
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId()), processEngine);

        // manual assignment task must be there...
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));
    }
}