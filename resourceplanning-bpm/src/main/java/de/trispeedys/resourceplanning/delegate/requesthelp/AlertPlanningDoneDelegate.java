package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;

public class AlertPlanningDoneDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // TODO trigger master process (if slave process will ever be started from there...)
    }
}