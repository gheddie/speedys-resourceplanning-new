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
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
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
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class SwapPositionsTest
{
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        Helper helperSourceOld = sourceAssignment.getHelper();
        HelperAssignment targetAssignment = assignments.get(1);
        Helper helperTargetOld = targetAssignment.getHelper();

        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);

        // both helpers agree...
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), true,
                TriggerComplexSwapMailTemplate.TRIGGER_SOURCE, processEngine.getProcessEngine());
        HelperConfirmation.processComplexSwapResponse(event2016.getId(), sourceAssignment.getPosition().getId(), targetAssignment.getPosition().getId(), true,
                TriggerComplexSwapMailTemplate.TRIGGER_TARGET, processEngine.getProcessEngine());

        // both helpers have agreed, so the assignments must have been swapped...
        assertEquals(targetAssignment.getPosition(), RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperSourceOld.getId(), event2016.getId()).getPosition());
        assertEquals(sourceAssignment.getPosition(), RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByHelperAndEvent(helperTargetOld.getId(), event2016.getId()).getPosition());
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
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
        assertTrue(helperAssignmentRepository.findById(sourceAssignment.getId()).isCancelled());
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);
        
        doPingPong(event2016);
        doPingPong(event2016);
        doPingPong(event2016);
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

    @Test
    @Deployment(resources =
    {
            "SwapPositions.bpmn", "RequestHelp.bpmn"
    })
    public void testHelperDeclinesSimpleSwap()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);

        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        finishHelperProcesses(event2016, helper);

        HelperAssignmentRepository helperAssignmentRepository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);

        List<HelperAssignment> assignments = helperAssignmentRepository.findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);

        Position unassignedPosition = RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event2016, true).get(0);
        startSwapProcess(event2016, sourceAssignment, unassignedPosition, true);

        // check if source assignment is 'CANCELLED' and target assignment is 'CONFIRMED' for the helper...
        assertTrue(helperAssignmentRepository.findById(sourceAssignment.getId()).isCancelled());
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
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016",
                        "TRI-2016", 21, 6, 2016, null, null);
        
        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll();
        finishHelperProcesses(event2016, helpers.get(0), helpers.get(1));

        List<HelperAssignment> assignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedHelperAssignments(event2016);

        HelperAssignment sourceAssignment = assignments.get(0);
        Helper helperSourceOld = sourceAssignment.getHelper();
        HelperAssignment targetAssignment = assignments.get(1);
        Helper helperTargetOld = targetAssignment.getHelper();

        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);
        
        // we have one swap in state 'REQUESTED'
        assertEquals(1, RepositoryProvider.getRepository(AssignmentSwapRepository.class).findRequestedByEvent(event2016).size());
        
        // start a swap with one of the above positions must lead to an exception...
        startSwapProcess(event2016, sourceAssignment, targetAssignment, false);
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
}