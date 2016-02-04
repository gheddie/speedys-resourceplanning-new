package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessageQueueRepository extends AbstractDatabaseRepository<MessageQueue> implements
        DatabaseRepository<MessageQueueRepository>
{
    private static final Logger logger = Logger.getLogger(MessageQueueRepository.class);

    private static final boolean INSTANT_SEND = true;

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
        String username = null;
        String password = null;
        String host = null;
        String port = null;
        try
        {
            username = AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_USER);
            password = AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_PASSWD);
            host = AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_HOST);
            port = AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_PORT);
            MailSender.sendHtmlMail(message.getToAddress(), message.getBody(), message.getSubject(), username, password, host, port);
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
            MessagingType messagingType, boolean doSend, Helper helper)
    {
        MessageQueue message =
                (MessageQueue) EntityFactory.buildMessageQueue(fromAddress, toAddress, subject, body, messagingType, helper).saveOrUpdate();
        if ((INSTANT_SEND) && (doSend))
        {
            // do it in a thread...
            sendUnprocessedMessage(message);
        }
    }

    public List<MessageQueue> findByHelperAndMessagingType(Helper helper, MessagingType messagingType)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MessageQueue.ATTR_MESSAGING_TYPE, messagingType);
        parameters.put(MessageQueue.ATTR_HELPER, helper);
        return dataSource().find(null, "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingType = :messagingType AND mq.helper = :helper", parameters);
    }
}