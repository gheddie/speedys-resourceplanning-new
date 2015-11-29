package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class MessageQueueRepository extends AbstractDatabaseRepository<MessageQueue> implements DatabaseRepository<MessageQueueRepository>
{
    public List<MessageQueue> findAllUnprocessedMessages()
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("messagingState", MessagingState.UNPROCESSED);
        return dataSource().find(
                "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = :messagingState",
                variables);
    }

    protected DefaultDatasource<MessageQueue> createDataSource()
    {
        return new MessageQueueDatasource();
    }
}