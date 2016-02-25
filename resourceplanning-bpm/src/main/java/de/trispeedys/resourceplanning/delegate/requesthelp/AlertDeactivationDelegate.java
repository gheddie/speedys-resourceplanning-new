package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.helprequest.AlertDeactivationMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class AlertDeactivationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));

        // do not use 'constructMessage()' directly, as the mail template needs 'BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION' set...
        AlertDeactivationMailTemplate template = (AlertDeactivationMailTemplate) getMessageTemplate(execution, helper, position, getEvent(execution));
        template.setDeactivationOnTimeout((boolean) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_DEACTIVATION_ON_TIMEOUT));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL),
                template.constructSubject(), template.constructBody(), template.getMessagingType(), true, null);
    }
}