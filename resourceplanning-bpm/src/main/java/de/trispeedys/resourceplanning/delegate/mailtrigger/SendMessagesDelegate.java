package de.trispeedys.resourceplanning.delegate.mailtrigger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.service.MessagingService;

public class SendMessagesDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        MessagingService.sendAllUnprocessedMessages();
    }
}