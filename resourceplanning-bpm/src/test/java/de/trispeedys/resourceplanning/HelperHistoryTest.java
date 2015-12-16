package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class HelperHistoryTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    // @Test
    // TODO Fix test --> CALLBACK_ASSIGNMENT_AS_BEFORE can not be tested because test do not use 'HelperInteraction'
    @Deployment(resources = "RequestHelp.bpmn")
    public void testProposePositionsOnAssignmentRequestedAsBefore()
    {
        HibernateUtil.clearAll();           
        
        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED,
                        EventTemplate.TEMPLATE_TRI);

        Event event2016 = SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
        
        Helper helperA = RepositoryProvider.getRepository(HelperRepository.class).findAll(null).get(0);
        
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        // callback --> as usual
        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);
    }
}