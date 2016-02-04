package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public class SetReentrancyDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POS_CHOOSING_REENTRANT, new Boolean(true));
    }
}