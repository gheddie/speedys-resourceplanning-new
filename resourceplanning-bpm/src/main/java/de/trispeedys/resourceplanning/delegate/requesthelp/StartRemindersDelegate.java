package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.HashMap;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;

public class StartRemindersDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpMaster.VAR_ACTIVE_HELPER_ID);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, helperId);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpMaster.VAR_MASTER_EVENT_ID);
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, eventId);
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);        
        execution.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }
}