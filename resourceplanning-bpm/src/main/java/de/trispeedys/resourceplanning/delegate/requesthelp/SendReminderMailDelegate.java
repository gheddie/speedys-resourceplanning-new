package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.helprequest.SendReminderMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

public class SendReminderMailDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper and event
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Long eventId = (Long) execution.getVariable(BpmVariables.Misc.VAR_EVENT_ID);
        Long positionId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION);
        // write mail
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(null, helperId);
        GuidedEvent event = (GuidedEvent) Datasources.getDatasource(GuidedEvent.class).findById(null, eventId);
        Position position = (Position) Datasources.getDatasource(Position.class).findById(null, positionId);
        int attemptCount = (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS);
        SendReminderMailTemplate template = new SendReminderMailTemplate(helper, event, position,
                (Boolean) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE), attemptCount);
        // send instantly for repeated attempts, do not for first mail...
        boolean doSend = (attemptCount == 0) ? false : true;
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(),
                template.constructBody(), getMessagingType(attemptCount), doSend, helper);
        // increase attempts
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, (attemptCount + 1));
    }

    private MessagingType getMessagingType(int attempt)
    {
        switch (attempt)
        {
            case 0:
                return MessagingType.REMINDER_STEP_0;
            case 1:
                return MessagingType.REMINDER_STEP_1;
            case 2:
                return MessagingType.REMINDER_STEP_2;
            default:
                return MessagingType.NONE;
        }
    }
}