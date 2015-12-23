package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.SendReminderMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class SendReminderMailDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper and event
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Long positionId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION);
        // write mail
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(null, helperId);
        Event event = (Event) Datasources.getDatasource(Event.class).findById(null, eventId);
        Position position = (Position) Datasources.getDatasource(Position.class).findById(null, positionId);
        int attemptCount = (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS);
        SendReminderMailTemplate template = new SendReminderMailTemplate(helper, event, position,
                (Boolean) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE), attemptCount);
        // send instantly for repeated attempts, do not for first mail...
        boolean doSend = (attemptCount == 0) ? false : true;
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(),
                template.constructBody(), getMessagingType(attemptCount), MessagingFormat.HTML, doSend);
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