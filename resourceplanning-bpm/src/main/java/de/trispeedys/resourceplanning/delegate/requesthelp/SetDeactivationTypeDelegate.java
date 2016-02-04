package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public class SetDeactivationTypeDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // yes, this was a deactivation on a time out...
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_DEACTIVATION_ON_TIMEOUT, true);
    }
}