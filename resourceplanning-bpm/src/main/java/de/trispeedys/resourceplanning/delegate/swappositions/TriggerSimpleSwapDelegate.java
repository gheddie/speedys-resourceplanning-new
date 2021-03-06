package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.messaging.TriggerSimpleSwapMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

public class TriggerSimpleSwapDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        TriggerSimpleSwapMailTemplate template = new TriggerSimpleSwapMailTemplate(getEvent(execution), getSourceAssignment(execution), getTargetPosition(execution));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", getSourceAssignment(execution).getHelper().getEmail(), template.constructSubject(),
                template.constructBody(), template.getMessagingType(), true, null);
    }
}