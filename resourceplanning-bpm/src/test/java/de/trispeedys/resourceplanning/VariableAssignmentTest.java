package de.trispeedys.resourceplanning;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import static org.junit.Assert.assertTrue;

public class VariableAssignmentTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    /**
     * Tests the variables value assignments for a simple run through the process with option
     * {@link HelperCallback#CHANGE_POS} chosen by the helper.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPositionAndAvailability()
    {
        // as always...
        HibernateUtil.clearAll();

        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED,
                        EventTemplate.TEMPLATE_TRI);

        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
        
        Helper helperA = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        // start process for on helper
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
                
        /**
         * 'CheckAvailabilityInitialDelegate' must have set variable 'VAR_PRIOR_POS_AVAILABLE' and 'VAR_PRIOR_POSITION',
         * but not 'VAR_CHOSEN_POSITION' and 'VAR_CHOSEN_POS_AVAILABLE'. These must only be used later, when the helper
         * has actively chosen a position. 
         */
        Execution execution = processEngine.getRuntimeService().createExecutionQuery().list().get(0);
        
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE) != null);
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION) != null);
        
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE) == null);
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) == null);
        
        // call back 'CHANGE POS'...
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKey, processEngine);
        
        // ...choose a position (all of them are available)...
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findAll().get(0);
        
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, PositionService.isPositionAvailable(event2016.getId(), position.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
        
        // now (and only now !!) variables 'VAR_CHOSEN_POSITION' and 'VAR_CHOSEN_POS_AVAILABLE' must have been set...
        // TODO why is the execution gone here? it should wait for 'SIG_EVENT_STARTED'...
        /*
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE) != null);
        assertTrue(processEngine.getRuntimeService().getVariable(execution.getId(), BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) != null);
        */
    }
}