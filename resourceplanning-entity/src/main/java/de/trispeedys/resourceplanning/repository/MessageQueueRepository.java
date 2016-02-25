package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.ThreadedMessageContainer;

public class MessageQueueRepository extends AbstractDatabaseRepository<MessageQueue> implements DatabaseRepository<MessageQueueRepository>
{
    private static final Logger logger = Logger.getLogger(MessageQueueRepository.class);

    private static final boolean INSTANT_SEND = true;

    public List<MessageQueue> findAllUnprocessedMessages()
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("messagingState", MessagingState.UNPROCESSED);
        return dataSource().find(null, "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = :messagingState",
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
                        MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = '" + MessagingState.FAILURE +
                        "' OR mq.messagingState = '" + MessagingState.UNPROCESSED + "' ORDER BY mq.creationTime DESC");
    }

    public void sendAllUnprocessedMessages()
    {
        logger.info("sending all unprocessed messages...");
        for (MessageQueue message : RepositoryProvider.getRepository(MessageQueueRepository.class).findAllUnprocessedMessages())
        {
            sendUnprocessedMessage(message);
        }
    }

    public void sendUnprocessedMessage(MessageQueue message)
    {
        new ThreadedMessageContainer(message, AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_USER),
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_PASSWD),
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.SMTP_HOST), AppConfiguration.getInstance()
                        .getConfigurationValue(AppConfigurationValues.SMTP_PORT)).start();
    }

    public void createMessage(String fromAddress, String toAddress, String subject, String body, MessagingType messagingType,
            boolean doSend, Helper helper)
    {
        MessageQueue message =
                (MessageQueue) EntityFactory.buildMessageQueue(fromAddress, toAddress, subject, body, messagingType, helper).saveOrUpdate();
        if ((INSTANT_SEND) && (doSend))
        {
            // do it in a thread...
            sendUnprocessedMessage(message);
        }
        else
        {
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@ message @@@@@@@@@@@@@@@@@@@@@ ");
            System.out.println("from : " + fromAddress);
            System.out.println("to : " + toAddress);
            System.out.println(" --------------------------------------------------- ");
            System.out.println(subject);
            System.out.println(" --------------------------------------------------- ");
            System.out.println(body);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
            System.out.println();
        }
    }

    public List<MessageQueue> findByHelperAndMessagingType(Helper helper, MessagingType messagingType)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MessageQueue.ATTR_MESSAGING_TYPE, messagingType);
        parameters.put(MessageQueue.ATTR_HELPER, helper);
        return dataSource().find(null,
                "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingType = :messagingType AND mq.helper = :helper",
                parameters);
    }
}