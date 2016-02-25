package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class AlertCancellationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Position position =
                RepositoryProvider.getRepository(PositionRepository.class).findById(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));
        constructMessage(execution, getHelper(execution), position, getEvent(execution), AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL));
    }
}