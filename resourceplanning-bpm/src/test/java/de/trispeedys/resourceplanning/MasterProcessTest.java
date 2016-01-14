package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class MasterProcessTest
{
    private static final String PROCESS_KEY_MASTER = "RequestHelpSystemProcess";

    private static final String PROCESS_KEY_SLAVE = "RequestHelpHelperProcess";

    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testMasterProcess()
    {
        TestUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6,
                        2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016", "TRI-2016", 21, 6,
                        2016, null, null);

        // there are 5 active helpers
        List<Helper> activeHelpers = RepositoryProvider.getRepository(HelperRepository.class).findActiveHelpers();
        assertEquals(5, activeHelpers.size());

        // start a master process
        HashMap<String, Object> variables = new HashMap<>();
        variables.put(BpmVariables.RequestHelpMaster.VAR_MASTER_EVENT_ID, event2016.getId());
        processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_KEY_MASTER, variables);

        // one master process and one slave for every active helper...
        assertTrue(checkProcessCount(1, 5));

        HelperInteraction.processReminderCallback(event2016.getId(), activeHelpers.get(0).getId(),
                HelperCallback.ASSIGNMENT_AS_BEFORE, processEngine.getProcessEngine());
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);

        // one master process and one slave for every active helper except the finished one...
        assertTrue(checkProcessCount(1, 4));
    }

    private boolean checkProcessCount(int masterProcessCount, int slaveProcessCount)
    {
        List<ProcessInstance> masters =
                processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processDefinitionKey(PROCESS_KEY_MASTER)
                        .list();
        List<ProcessInstance> slaves =
                processEngine.getRuntimeService()
                        .createProcessInstanceQuery()
                        .processDefinitionKey(PROCESS_KEY_SLAVE)
                        .list();
        return ((masters.size() == masterProcessCount) && (slaves.size() == slaveProcessCount));
    }
}