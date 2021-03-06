package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.messaging.template.helprequest.DeactivationRecoveryMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

public class SendDeactivationRequestDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        DeactivationRecoveryMailTemplate template =
                new DeactivationRecoveryMailTemplate(getHelper(execution), getEvent(execution));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                helper.getEmail(), template.constructSubject(), template.constructBody(), template.getMessagingType(),
                true, helper);
    }
}