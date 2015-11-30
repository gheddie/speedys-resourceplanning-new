package de.trispeedys.resourceplanning.dto;

public class MessageDTO
{
    private String recipient;
    
    private String subject;
    
    private String body;

    private String messagingState;

    public String getRecipient()
    {
        return recipient;
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
    
    public String getMessagingState()
    {
        return messagingState;
    }

    public void setMessagingState(String messagingState)
    {
        this.messagingState = messagingState;
    }
}