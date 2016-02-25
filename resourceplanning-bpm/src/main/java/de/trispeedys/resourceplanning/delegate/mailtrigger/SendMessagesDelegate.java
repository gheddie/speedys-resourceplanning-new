package de.trispeedys.resourceplanning.delegate.mailtrigger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

public class SendMessagesDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        RepositoryProvider.getRepository(MessageQueueRepository.class).sendAllUnprocessedMessages();
    }
}