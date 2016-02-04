package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.HashMap;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public class StartRemindersDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpMaster.VAR_ACTIVE_HELPER_ID);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, helperId);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpMaster.VAR_MASTER_EVENT_ID);
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, eventId);
        String businessKey =
                BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);        
        execution.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }
}