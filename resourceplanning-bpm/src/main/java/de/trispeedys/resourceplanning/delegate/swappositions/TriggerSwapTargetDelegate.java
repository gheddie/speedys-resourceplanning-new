package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.messaging.template.swap.TriggerComplexSwapTargetMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

public class TriggerSwapTargetDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        TriggerComplexSwapTargetMailTemplate template = new TriggerComplexSwapTargetMailTemplate(getEvent(execution), getSourceAssignment(execution), getTargetAssignment(execution));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de", getTargetAssignment(execution).getHelper().getEmail(), template.constructSubject(),
                template.constructBody(), template.getMessagingType(), true, null);
    }
}