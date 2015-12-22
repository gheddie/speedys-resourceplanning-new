package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;

public class ConfirmManualAssignmentDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // TODO test execution in unit test
        Helper helper = getHelper(execution);
        constructMessage(execution, helper, null, getEvent(execution), helper.getEmail());
    }
}