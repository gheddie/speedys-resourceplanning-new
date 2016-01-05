package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessageQueueRepository extends AbstractDatabaseRepository<MessageQueue> implements
        DatabaseRepository<MessageQueueRepository>
{
    private static final Logger logger = Logger.getLogger(MessageQueueRepository.class);

    private static final boolean INSTANT_SEND = false;

    public List<MessageQueue> findAllUnprocessedMessages()
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("messagingState", MessagingState.UNPROCESSED);
        return dataSource().find(null,
                "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = :messagingState",
                variables);
    }

    protected DefaultDatasource<MessageQueue> createDataSource()
    {
        return new MessageQueueDatasource();
    }

    public List<MessageQueue> findUnsentMessages()
    {
        return dataSource().find(
                null,
                "FROM " +
                        MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = '" +
                        MessagingState.FAILURE + "' OR mq.messagingState = '" + MessagingState.UNPROCESSED + "'");
    }

    public void sendAllUnprocessedMessages()
    {
        logger.info("sending all unprocessed messages...");
        for (MessageQueue message : RepositoryProvider.getRepository(MessageQueueRepository.class)
                .findAllUnprocessedMessages())
        {
            sendUnprocessedMessage(message);
        }
    }

    public void sendUnprocessedMessage(MessageQueue message)
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
            logger.info("message [" +
                    message.getMessagingType() + "] succesfully sent to '" + message.getToAddress() + "'...");
        }
        catch (Exception e)
        {
            message.setMessagingState(MessagingState.FAILURE);
            logger.info("ERROR on sending message [" +
                    message.getMessagingType() + "] to '" + message.getToAddress() + "'...");
        }
        finally
        {
            message.saveOrUpdate();
        }
    }

    public void createMessage(String fromAddress, String toAddress, String subject, String body,
            MessagingType messagingType, MessagingFormat messagingFormat, boolean doSend)
    {
        MessageQueue message =
                (MessageQueue) EntityFactory.buildMessageQueue(fromAddress, toAddress, subject, body, messagingType,
                        messagingFormat).saveOrUpdate();
        if ((INSTANT_SEND) && (doSend))
        {
            sendUnprocessedMessage(message);
        }
    }
}