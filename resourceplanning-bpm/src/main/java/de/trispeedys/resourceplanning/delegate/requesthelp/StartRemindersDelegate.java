package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public class StartRemindersDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_ACTIVE_HELPER_ID);
        execution.getProcessEngineServices().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG);
    }
}