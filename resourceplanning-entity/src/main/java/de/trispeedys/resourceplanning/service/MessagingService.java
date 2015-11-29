package de.trispeedys.resourceplanning.service;

import org.apache.log4j.Logger;

import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessagingService
{
    private static final Logger logger = Logger.getLogger(MessagingService.class);

    private static final boolean INSTANT_SEND = false;

    public static void sendAllUnprocessedMessages()
    {
        logger.info("sending all unprocessed messages...");
        for (MessageQueue message : RepositoryProvider.getRepository(MessageQueueRepository.class).findAllUnprocessedMessages())
        {
            sendUnprocessedMessage(message);
        }
    }

    public static void sendUnprocessedMessage(MessageQueue message)
    {
        try
        {
            switch (message.getMessagingFormat())
            {
                case PLAIN:
                    MailSender.sendMail(message.getToAddress(), message.getBody(), message.getSubject());
                    break;
                case HTML:
                    MailSender.sendHtmlMail(message.getToAddress(), message.getBody(), message.getSubject());
                    break;
            }
            message.setMessagingState(MessagingState.PROCESSED);
            logger.info("message [" + message.getMessagingType() + "] succesfully sent to '" + message.getToAddress() + "'...");
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

    public static void createMessage(String fromAddress, String toAddress, String subject, String body, MessagingType messagingType,
            MessagingFormat messagingFormat)
    {
        MessageQueue message =
                (MessageQueue) EntityFactory.buildMessageQueue(fromAddress, toAddress, subject, body, messagingType, messagingFormat).saveOrUpdate();
        if (INSTANT_SEND)
        {
            sendUnprocessedMessage(message);
        }
    }
}