package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.util.StringUtil;

public class ConfirmManualAssignmentDelegate extends RequestHelpNotificationDelegate
{
    private static final int MAX_COMMENT_LENGTH = 250;

    public void execute(DelegateExecution execution) throws Exception
    {
        // TODO test execution in unit test
        Helper helper = getHelper(execution);
        constructMessage(execution, helper, null, getEvent(execution), helper.getEmail());
        String wish = (String) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_MANUAL_ASSIGNMENT_WISH);
        // create manual assignment comment
        if (!(StringUtil.isBlank(wish)))
        {
            if (wish.length() > MAX_COMMENT_LENGTH)
            {
                wish = wish.substring(0, MAX_COMMENT_LENGTH);
            }
            EntityFactory.buildManualAssignmentComment(getEvent(execution), helper, wish).saveOrUpdate();
        }
    }
}