package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.StringUtil;

public class BookingConfirmationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        if (StringUtil.isBlank(helper.getEmail()))
        {
            // may be this helper has no mail address...
            return;
        }
        constructMessage(execution, helper, RepositoryProvider.getRepository(
                PositionRepository.class).findById(
                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)), getEvent(execution), helper.getEmail());
    }
}