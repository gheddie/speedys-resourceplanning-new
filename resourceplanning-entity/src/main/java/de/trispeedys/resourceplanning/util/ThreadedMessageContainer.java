package de.trispeedys.resourceplanning.util;

import org.apache.log4j.Logger;

import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

public class ThreadedMessageContainer extends Thread
{
    private static final Logger logger = Logger.getLogger(ThreadedMessageContainer.class);

    private MessageQueue message;

    private String username;

    private String password;

    private String host;

    private String port;

    public ThreadedMessageContainer(MessageQueue aMessage, String aUsername, String aPassword, String aHost, String aPort)
    {
        super();
        this.message = aMessage;
        this.username = aUsername;
        this.password = aPassword;
        this.host = aHost;
        this.port = aPort;
    }

    public void run()
    {
        try
        {
            MailSender.sendHtmlMail(message.getToAddress(), message.getBody(), message.getSubject(), username, password, host, port);
            message.setMessagingState(MessagingState.PROCESSED);
        }
        catch (Exception e)
        {
            message.setMessagingState(MessagingState.FAILURE);
            logger.info("ERROR on sending message [" + message.getMessagingType() + "] to '" + message.getToAddress() + "'...");
        }
        finally
        {
            message.saveOrUpdate();
        }
    }
}