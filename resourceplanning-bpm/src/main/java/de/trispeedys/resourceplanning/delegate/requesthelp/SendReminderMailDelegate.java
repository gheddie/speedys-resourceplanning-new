package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.SendReminderMailTemplate;
import de.trispeedys.resourceplanning.service.MessagingService;

public class SendReminderMailDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper and event
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Long positionId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION);
        // write mail
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        Event event = (Event) Datasources.getDatasource(Event.class).findById(eventId);
        Position position = (Position) Datasources.getDatasource(Position.class).findById(positionId);
        int attemptCount = (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS);
        SendReminderMailTemplate template = new SendReminderMailTemplate(helper, event, position,
                (Boolean) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE), attemptCount);
        MessagingService.createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(),
                template.constructBody(), getMessagingType(attemptCount), MessagingFormat.HTML);
        // increase attempts
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, (attemptCount + 1));

        // write history entry...
        writeHistoryEntry(HistoryType.REMINDER_MAIL_SENT, execution);
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