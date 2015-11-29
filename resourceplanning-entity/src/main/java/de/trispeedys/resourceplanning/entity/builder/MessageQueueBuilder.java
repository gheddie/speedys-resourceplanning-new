package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

public class MessageQueueBuilder extends AbstractEntityBuilder<MessageQueue>
{
    private String fromAddress;
    
    private String toAddress;
    
    private String subject;
    
    private String body;

    private MessagingState messagingState;

    private MessagingType messagingType;

    private MessagingFormat messagingFormat;

    public MessageQueueBuilder withFromAddress(String aFromAddress)
    {
        fromAddress = aFromAddress;
        return this;
    }

    public MessageQueueBuilder withToAddress(String aToAddress)
    {
        toAddress = aToAddress;
        return this;
    }

    public MessageQueueBuilder withSubject(String aSubject)
    {
        subject = aSubject;
        return this;
    }

    public MessageQueueBuilder withBody(String aBody)
    {
        body = aBody;
        return this;
    }
    
    public MessageQueueBuilder withMessagingState(MessagingState aMessagingState)
    {
        messagingState = aMessagingState;
        return this;
    }
    
    public MessageQueueBuilder withMessagingType(MessagingType aMessagingType)
    {
        messagingType = aMessagingType;
        return this;
    }
    
    public MessageQueueBuilder withMessagingFormat(MessagingFormat aMessagingFormat)
    {
        messagingFormat = aMessagingFormat;
        return this;
    }
    
    public MessageQueue build()
    {
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setFromAddress(fromAddress);
        messageQueue.setToAddress(toAddress);
        messageQueue.setSubject(subject);
        messageQueue.setBody(body);
        messageQueue.setMessagingState(MessagingState.UNPROCESSED);
        messageQueue.setMessagingType(messagingType);
        messageQueue.setMessagingFormat(messagingFormat);
        messageQueue.setCreationTime(new Date());
        return messageQueue;
    }
}