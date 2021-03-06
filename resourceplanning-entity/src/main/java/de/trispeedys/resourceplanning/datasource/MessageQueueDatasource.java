package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.MessageQueue;

public class MessageQueueDatasource extends DefaultDatasource<MessageQueue>
{
    protected Class<MessageQueue> getGenericType()
    {
        return MessageQueue.class;
    }
}