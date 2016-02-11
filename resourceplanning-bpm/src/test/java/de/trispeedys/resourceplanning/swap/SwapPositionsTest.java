package de.trispeedys.resourceplanning.swap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningSwapException;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.HelperConfirmation;
import de.trispeedys.resourceplanning.messaging.template.swap.TriggerComplexSwapMailTemplate;
import de.trispeedys.resourceplanning.repository.AssignmentSwapRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class SwapPositionsTest
{
    private static final String FIRSTNAME_MAIKE = "Maike";
    
    private static final String FIRSTNAME_ELSA = "Elsa";
    
    private static final String FIRSTNAME_HOLGER = "Holger";
    
    private static final String FIRSTNAME_WIEBKE = "Wiebke";
    
    private static final String FIRSTNAME_STEFAN = "Stefan";
    
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testComplexSwapWithMutualAgreement()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        Helper helperSource = (Helper) Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_FIRST_NAME, FIRSTNAME_ELSA).get(0);
        Helper helperTarget = (Helper) Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_FIRST_NAME, FIRSTNAME_MAIKE).get(0);
        
        finishHelperProcesses(event2016, helperSource, helperTarget);

        HelperAssignmentRepository helperAssignmentRepository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        
        HelperAssignment sourceAssignment = helperAssignmentRepository.findByHelperAndEvent(helperSource, event2016);        
        String mailAddressSource = helperSource.getEmail();
                
        HelperAssignment targetAssignment = helperAssignmentRepository.findByHelperAndEvent(helperTarget, event2016);
        String mailAddressTarget = helperTarget.getEmail();

        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);

        // make sure each helper gets a mail ---> check message repo for that (query for a message by the helpers
// mails)!!
        assertEquals(1,
                Datasources.getDatasource(MessageQueue.class)
                        .find(null, MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.COMPLEX_SWAP_SOURCE_TRIGGER, MessageQueue.ATTR_TO_ADDRESS, mailAddressSource)
                        .size());
        assertEquals(1,
                Datasources.getDatasource(MessageQueue.class)
                        .find(null, MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.COMPLEX_SWAP_TARGET_TRIGGER, MessageQueue.ATTR_TO_ADDRESS, mailAddressTarget)
                        .size());

        // both helpers agree...
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), true,
                TriggerComplexSwapMailTemplate.TRIGGER_SOURCE, processEngine.getProcessEngine());
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), true,
                TriggerComplexSwapMailTemplate.TRIGGER_TARGET, processEngine.getProcessEngine());

        // both helpers have agreed, so the assignments must have been swapped...
        assertEquals(targetAssignment.getPosition(), helperAssignmentRepository.findByHelperAndEvent(helperSource.getId(), event2016.getId()).getPosition());
        assertEquals(sourceAssignment.getPosition(), helperAssignmentRepository.findByHelperAndEvent(helperTarget.getId(), event2016.getId()).getPosition());

        // TODO there must be 3 mails of type 'SWAP_RESULT' (admin an botg helpers)...
        assertEquals(1, Datasources.getDatasource(MessageQueue.class).find(null, MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.SWAP_RESULT, MessageQueue.ATTR_TO_ADDRESS, mailAddressSource).size());
        assertEquals(1, Datasources.getDatasource(MessageQueue.class).find(null, MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.SWAP_RESULT, MessageQueue.ATTR_TO_ADDRESS, mailAddressTarget).size());
        assertEquals(
                1,
                Datasources.getDatasource(MessageQueue.class)
                        .find(null, MessageQueue.ATTR_MESSAGING_TYPE, MessagingType.SWAP_RESULT, MessageQueue.ATTR_TO_ADDRESS,
                                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL))
                        .size());
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testAdminInterruptsComplexSwap()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        HelperAssignment targetAssignment = assignments.get(1);

        ProcessInstance execution = startSwapProcess(event2016, sourceAssignment, targetAssignment, false);

        // one of the helpers (source assignment) agrees...
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), true,
                TriggerComplexSwapMailTemplate.TRIGGER_SOURCE, processEngine.getProcessEngine());

        // now, admin uses the killswitch...
        processEngine.getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_KILL_SWAP, execution.getBusinessKey());

        // there should be a interrupted assignment swap
        assertEquals(
                1,
                RepositoryProvider.getRepository(AssignmentSwapRepository.class)
                        .findByEventAndPositionsAndResult(event2016, sourceAssignment.getPosition(), targetAssignment.getPosition(), SwapState.INTERRUPTED)
                        .size());

        // TODO make sure nothing has changed...
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testComplexSwapBySystem()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        Helper helperSourceOld = sourceAssignment.getHelper();
        HelperAssignment targetAssignment = assignments.get(1);
        Helper helperTargetOld = targetAssignment.getHelper();

        startSwapProcess(event2016, sourceAssignment, targetAssignment, true);

        // assignments must have been swapped...
        assertEquals(targetAssignment.getPosition(), RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperSourceOld.getId(), event2016.getId()).getPosition());
        assertEquals(sourceAssignment.getPosition(), RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperTargetOld.getId(), event2016.getId()).getPosition());
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testComplexSwapWithDisagreement()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        HelperAssignment targetAssignment = assignments.get(1);

        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);

        // one helper disagrees...
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), false,
                TriggerComplexSwapMailTemplate.TRIGGER_SOURCE, processEngine.getProcessEngine());

        // the created assignment swap must be in state 'REJECTED'...
        List<AssignmentSwap> swaps = RepositoryProvider.getRepository(AssignmentSwapRepository.class).findAll();
        assertEquals(1, swaps.size());
        assertEquals(SwapState.REJECTED, swaps.get(0).getSwapState());

        // process should be gone...
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testHelperAgreesToSimpleSwap()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);

        HelperAssignmentRepository helperAssignmentRepository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);

        List<HelperAssignment> assignments = helperAssignmentRepository.findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);

        Position unassignedPosition = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, true).get(0);
        startSwapProcess(event2016, sourceAssignment, unassignedPosition, false);

        // helper agrees...
        HelperConfirmation.processSimpleSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), unassignedPosition.getId(), true, processEngine.getProcessEngine());

        // check if source assignment is 'CANCELLED' and target assignment is 'CONFIRMED' for the helper...
        // TODO assignment is no longer cacelled in sinple swap
        // assertTrue(helperAssignmentRepository.findById(sourceAssignment.getId()).isCancelled());
        assertTrue(helperAssignmentRepository.findByHelperAndEventAndPosition(helper, event2016, unassignedPosition).isConfirmed());

        // assignment swap must now be in state 'COMPLETED'...
        List<AssignmentSwap> swaps = RepositoryProvider.getRepository(AssignmentSwapRepository.class).findAll();
        assertEquals(1, swaps.size());
        assertEquals(SwapState.COMPLETED, swaps.get(0).getSwapState());
    }

    // @Test
    // TODO why does it not work?
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testSimpleSwapAgreementPingPong()
    {
        TestUtil.clearAll();
        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);

        doPingPong(event2016);
        doPingPong(event2016);
        doPingPong(event2016);
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testHelperDeclinesSimpleSwap()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);

        HelperAssignmentRepository helperAssignmentRepository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);

        List<HelperAssignment> assignments = helperAssignmentRepository.findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);

        Position unassignedPosition = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, true).get(0);
        startSwapProcess(event2016, sourceAssignment, unassignedPosition, false);

        // helper agrees...
        HelperConfirmation.processSimpleSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), unassignedPosition.getId(), false, processEngine.getProcessEngine());

        // helper must still be confirmed on his old position...
        assertTrue(helperAssignmentRepository.findByHelperAndEventAndPosition(helper, event2016, sourceAssignment.getPosition()).isConfirmed());

        // there must be an assignment for formerly unassigned position
        assertTrue(helperAssignmentRepository.findByEventAndPosition(event2016, unassignedPosition) == null);
    }

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testSimpleSwapBySystem()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);

        HelperAssignmentRepository helperAssignmentRepository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);

        List<HelperAssignment> assignments = helperAssignmentRepository.findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);

        Position unassignedPosition = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, true).get(0);
        startSwapProcess(event2016, sourceAssignment, unassignedPosition, true);

        // check if source assignment is 'CANCELLED' and target assignment is 'CONFIRMED' for the helper...
        // TODO assignment is no longer cacelled in sinple swap        
        // assertTrue(helperAssignmentRepository.findById(sourceAssignment.getId()).isCancelled());
        assertTrue(helperAssignmentRepository.findByHelperAndEventAndPosition(helper, event2016, unassignedPosition).isConfirmed());
    }

    @Test(expected = ResourcePlanningSwapException.class)
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testRequestedSwapCollision()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(createEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        HelperAssignment targetAssignment = assignments.get(1);

        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);

        // we have one swap in state 'REQUESTED'
        assertEquals(1, RepositoryProvider.getRepository(AssignmentSwapRepository.class).findRequestedByEvent(event2016).size());

        // start a swap with one of the above positions must lead to an exception...
        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);
    }
    
    private void doPingPong(Event event)
    {
        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event);
        HelperAssignment sourceAssignment = assignments.get(0);
        Position unassignedPosition = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event, true).get(0);
        startSwapProcess(event, sourceAssignment, unassignedPosition, false);
        // helper agrees...
        HelperConfirmation.processSimpleSwapResponse(event.getId(), sourceAssignment.getPosition().getId(), unassignedPosition.getId(), true, processEngine.getProcessEngine());
    }

    private ProcessInstance startSwapProcess(Event event2016, HelperAssignment sourceAssignment, HelperAssignment targetAssignment, boolean swapBySystem)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.Swap.VAR_SWAP_BY_SYSTEM, swapBySystem);
        variables.put(BpmVariables.Swap.VAR_POS_ID_SOURCE, sourceAssignment.getPosition().getId());
        variables.put(BpmVariables.Swap.VAR_POS_ID_TARGET, targetAssignment.getPosition().getId());
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, event2016.getId());
        String businessKey = BusinessKeys.generateSwapBusinessKey(event2016, sourceAssignment.getPosition(), targetAssignment.getPosition());
        return processEngine.getRuntimeService().startProcessInstanceByMessage(BpmMessages.Swap.MSG_START_SWAP, businessKey, variables);
    }

    private ProcessInstance startSwapProcess(Event event2016, HelperAssignment sourceAssignment, Position position, boolean swapBySystem)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.Swap.VAR_SWAP_BY_SYSTEM, swapBySystem);
        variables.put(BpmVariables.Swap.VAR_POS_ID_SOURCE, sourceAssignment.getPosition().getId());
        variables.put(BpmVariables.Swap.VAR_POS_ID_TARGET, position.getId());
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, event2016.getId());
        String businessKey = BusinessKeys.generateSwapBusinessKey(event2016, sourceAssignment.getPosition(), position);
        return processEngine.getRuntimeService().startProcessInstanceByMessage(BpmMessages.Swap.MSG_START_SWAP, businessKey, variables);
    }

    private void finishHelperProcesses(Event event2016, Helper... helpers)
    {
        for (Helper helper : helpers)
        {
            RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, BusinessKeys.generateRequestHelpBusinessKey(helper, event2016), processEngine);
            HelperConfirmation.processAssignmentAsBeforeConfirmation(event2016.getId(), helper.getId(), null, processEngine.getProcessEngine());
        }
        // take over
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
    }
    
    private static Event createEvent(String description, String eventKey, int day, int month, int year, EventState eventState, String templateName)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123ggg").saveOrUpdate();

        // build event
        Event myLittleEvent = EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.FINISHED, template, null).saveOrUpdate();
        // create helpers
        Helper helper1 = EntityFactory.buildHelper("Mueller", FIRSTNAME_STEFAN, "helper.stefan.mueller@helpers.de", HelperState.ACTIVE, 1, 2, 1980, true).saveOrUpdate();
        Helper helper2 = EntityFactory.buildHelper("Peters", FIRSTNAME_WIEBKE, "helper.wiebke@peters.de", HelperState.ACTIVE, 2, 2, 1980, true).saveOrUpdate();
        Helper helper3 = EntityFactory.buildHelper("Klausen", FIRSTNAME_HOLGER, "helper.holger.klausen@helpers.de", HelperState.ACTIVE, 3, 2, 1980, true).saveOrUpdate();
        Helper helper4 = EntityFactory.buildHelper("Goldap", FIRSTNAME_ELSA, "helper.elsa.goldap@helpers.de", HelperState.ACTIVE, 4, 2, 1980, true).saveOrUpdate();
        Helper helper5 = EntityFactory.buildHelper("Behndorf", FIRSTNAME_MAIKE, "helper.maike.behndorf@helpers.de", HelperState.ACTIVE, 5, 2, 1980, true).saveOrUpdate();
        // build domains
        Domain domainSwim = EntityFactory.buildDomain("Schimmstrecke", 1).saveOrUpdate();
        Domain domainRun = EntityFactory.buildDomain("Laufstrecke", 2).saveOrUpdate();
        // build positions
        Position pos1 = EntityFactory.buildPosition("Helferausstieg", 12, domainSwim, 0, true).saveOrUpdate();
        Position pos2 = EntityFactory.buildPosition("Badekappen sammeln", 12, domainSwim, 1, true).saveOrUpdate();
        Position pos3 = EntityFactory.buildPosition("Uebergang Herrenfeldtstrasse", 12, domainRun, 2, true).saveOrUpdate();
        Position pos4 = EntityFactory.buildPosition("Einweisung Zielkanal", 12, domainRun, 3, true).saveOrUpdate();
        Position pos5 = EntityFactory.buildPosition("Kontrolle Abwurfzone", 12, domainRun, 4, true).saveOrUpdate();
        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);
        // assign helpers to positions
        SpeedyRoutines.assignHelperToPositions(helper1, myLittleEvent, pos1);
        SpeedyRoutines.assignHelperToPositions(helper2, myLittleEvent, pos2);
        SpeedyRoutines.assignHelperToPositions(helper3, myLittleEvent, pos3);
        SpeedyRoutines.assignHelperToPositions(helper4, myLittleEvent, pos4);
        SpeedyRoutines.assignHelperToPositions(helper5, myLittleEvent, pos5);

        return myLittleEvent;
    }    
}