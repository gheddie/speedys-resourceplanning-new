package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.messaging.ConfirmPauseMailTemplate;
import de.trispeedys.resourceplanning.service.MessagingService;

public class ConfirmPauseDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // send mail
        Helper helper = getHelper(execution);
        ConfirmPauseMailTemplate template =
                new ConfirmPauseMailTemplate(helper);
        MessagingService.createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(),
                template.constructBody(), template.getMessagingType(), template.getMessagingFormat());
    }
}