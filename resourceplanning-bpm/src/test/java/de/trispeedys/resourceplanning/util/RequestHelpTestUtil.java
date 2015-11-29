package de.trispeedys.resourceplanning.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperHistory;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;
import de.trispeedys.resourceplanning.execution.BpmJobDefinitions;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperHistoryRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.PositionService;

public class RequestHelpTestUtil
{
    public static final String PROCESS_DEFINITION_KEY_HELPER_PROCESS = "RequestHelpHelperProcess";

    public static final String PROCESS_DEFINITION_KEY_SYSTEM_PROCESS = "RequestHelpSystemProcess";

    /**
     * Starts process with helper and for follow up assignment with given helper id, event id and business key.
     * 
     * @param helper
     * @param businessKey
     * @param rule
     * @return 
     */
    public static ProcessInstance startHelperRequestProcess(Helper helper, Event event, String businessKey, ProcessEngineRule rule)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        return rule.getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }

    public static void startTriggerHelperProcess(ProcessEngineRule rule)
    {
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY_SYSTEM_PROCESS);
    }

    public static int countMails()
    {
        return RepositoryProvider.getRepository(MessageQueueRepository.class).findAll().size();
    }

    public static boolean checkMails(int expectedMailCount, MessagingType... types)
    {
        List<MessageQueue> messages = RepositoryProvider.getRepository(MessageQueueRepository.class).findAll();
        if ((messages == null) || (messages.size() != expectedMailCount))
        {
            return false;
        }
        // all expected mail types must be there...
        for (MessagingType type : types)
        {
            if (!(messageTypePresent(type, messages)))
            {
                   return false;
            }
        }
        // finally...
        return true;
    }

    public static boolean messageTypePresent(MessagingType type, List<MessageQueue> messages)
    {
        for (MessageQueue q : messages)
        {
            if (q.getMessagingType().equals(type))
            {
                return true;
            }
        }
        // type is not there...
        return false;
    }

    /**
     * Starts a request process and fire all timers, as the given helper does not respond to any mail.
     * 
     * @param event
     * @param helper
     * @param businessKey
     * @param processEngine
     */
    public static void doNotRespondToAnything(Event event, Helper helper, String businessKey, ProcessEngineRule processEngine)
    {
        RequestHelpTestUtil.startHelperRequestProcess(helper, event, businessKey, processEngine);
        // a mail for every helper must have been sent
        assertEquals(1, RequestHelpTestUtil.countMails());
        // one week is gone...
        fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);
        // there is a second mail
        assertEquals(2, RequestHelpTestUtil.countMails());
        fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);
        // there is a third mail
        assertEquals(3, RequestHelpTestUtil.countMails());
        fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);

        // user was asked if he wants to deactivated permanently (so 3 mails
        // [REMINDER_STEP_0-2 and DEACTIVATION_REQUEST] should be there)
        assertTrue(RequestHelpTestUtil.checkMails(4, MessagingType.REMINDER_STEP_0, MessagingType.REMINDER_STEP_1,
                MessagingType.REMINDER_STEP_2, MessagingType.DEACTIVATION_REQUEST));
    }

    public static void fireTimer(String jobDefinition, ProcessEngineRule processEngine)
    {
        processEngine.getManagementService().executeJob(processEngine.getManagementService().createJobQuery().activityId(jobDefinition).list().get(0).getId());
    }
    
    public static void doCallback(HelperCallback helperCallback, String businessKey, ProcessEngineRule processEngine)
    {
        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, helperCallback);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variablesCallback);
    }
    
    public static void choosePosition(String businessKey, Position position, ProcessEngineRule processEngine, Long eventId)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, PositionService.isPositionAvailable(eventId, position.getId()));
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
    }
    
    public static boolean wasTaskGenerated(String taskId, ProcessEngineRule rule)
    {
        List<Task> list = rule.getTaskService().createTaskQuery().taskDefinitionKey(taskId).list();
        return ((list != null) && (list.size() > 0));
    }

    public static boolean checkHistory(HistoryType[] historyTypes, Helper helper, Event event)
    {
        List<HelperHistory> actualEntries = RepositoryProvider.getRepository(HelperHistoryRepository.class).findOrdered(helper, event);
        // length must be equal...
        if (historyTypes.length != actualEntries.size())
        {
            return false;
        }
        // every entry must be equal !!
        for (int index=0;index<historyTypes.length;index++)
        {
            if (!(historyTypes[index].equals(actualEntries.get(index).getHistoryType())))
            {
                return false;
            }
        }
        // OK
        return true;
    }
}