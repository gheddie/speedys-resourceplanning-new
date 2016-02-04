package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.messaging.template.swap.TriggerComplexSwapSourceMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class TriggerSwapSourceDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        TriggerComplexSwapSourceMailTemplate template = new TriggerComplexSwapSourceMailTemplate(getEvent(execution), getSourceAssignment(execution), getTargetAssignment(execution));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", getSourceAssignment(execution).getHelper().getEmail(), template.constructSubject(),
                template.constructBody(), template.getMessagingType(), true, null);
    }
}