package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.messaging.template.ConfirmPauseMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class ConfirmPauseDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // send mail
        Helper helper = getHelper(execution);
        ConfirmPauseMailTemplate template = new ConfirmPauseMailTemplate(helper);
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                helper.getEmail(), template.constructSubject(), template.constructBody(), template.getMessagingType(),
                true, helper);
    }
}