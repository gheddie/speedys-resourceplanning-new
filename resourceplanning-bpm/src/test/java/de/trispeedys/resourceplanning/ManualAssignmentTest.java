package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class ManualAssignmentTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testManualAssignments()
    {
        HibernateUtil.clearAll();

        Event event2015 =
                TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null,
                        null);

        // additional helpers
        EntityFactory.buildHelper("AAA", "BBB", "CCC", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("AAA", "FFF", "CCC", HelperState.INACTIVE, 1, 1, 1980).saveOrUpdate();

        String businessKey = null;
        List<Helper> activeHelpers =
                RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        for (Helper helper : activeHelpers)
        {
            businessKey =
                    ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
            RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);
        }

        List<Task> taskList =
                processEngine.getTaskService()
                        .createTaskQuery()
                        .taskDefinitionKey(
                                BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                        .list();
        assertEquals(1, taskList.size());
    }
}