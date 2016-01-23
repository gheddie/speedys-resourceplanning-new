package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.interaction.HelperConfirmation;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;
import de.trispeedys.resourceplanning.webservice.ResourceInfo;

public class PostProcessingTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    /**
     * The attempt to swap positions with having used signal {@link BpmSignals.RequestHelpHelper#SIG_EVENT_STARTED} must work.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testSwapPositions()
    {
        // clear db
        TestUtil.clearAll();
        
        // create events
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null, EventState.INITIATED);
        
        // get helpers
        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll(null);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        
        // start processes
        String businessKeyA = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKeyA, processEngine);
        String businessKeyB = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperB.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperB, event2016, businessKeyB, processEngine);        
        
        // assignemt as before
        HelperConfirmation.processAssignmentAsBeforeConfirmation(event2016.getId(), helperA.getId(), null, processEngine.getProcessEngine());
        HelperConfirmation.processAssignmentAsBeforeConfirmation(event2016.getId(), helperB.getId(), null, processEngine.getProcessEngine());
        
        // start post processing
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);           
        
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        HelperAssignment assignmentA = repository.findByHelperAndEvent(helperA, event2016);
        HelperAssignment assignmentB = repository.findByHelperAndEvent(helperB, event2016);
        new ResourceInfo().swapPositions(helperA.getId(), helperB.getId(), event2016.getId());
        
        // TODO
        assertEquals(assignmentB.getPosition(), repository.findByHelperAndEvent(helperA, event2016).getPosition());        
        assertEquals(assignmentA.getPosition(), repository.findByHelperAndEvent(helperB, event2016).getPosition());
    }
    
    /**
     * The attempt to swap positions without having used signal {@link BpmSignals.RequestHelpHelper#SIG_EVENT_STARTED} must fail.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testSwapPositionsNoCleanUp()
    {
        // TODO
    }
}