package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.HelperHistory;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class Datasources
{
    private static Datasources instance;
    
    private HashMap<Class<? extends AbstractDbObject>, DefaultDatasource> registeredDatasources;

    private DefaultDatasource defaultDatasource;

    private Datasources()
    {
        registeredDatasources = new HashMap<Class<? extends AbstractDbObject>, DefaultDatasource>();
        registerDatasources();
//        defaultDatasource = new DefaultDatasource();
    }

    private void registerDatasources()
    {
        registeredDatasources.put(Helper.class, new HelperDatasource());
        registeredDatasources.put(HelperHistory.class, new HelperHistoryDatasource());
        registeredDatasources.put(MessageQueue.class, new MessageQueueDatasource());
        registeredDatasources.put(Domain.class, new DomainDatasource());
        registeredDatasources.put(Position.class, new PositionDatasource());
        registeredDatasources.put(EventTemplate.class, new EventTemplateDatasource());
        registeredDatasources.put(Event.class, new EventDatasource());
        registeredDatasources.put(EventPosition.class, new EventPositionDatasource());
        registeredDatasources.put(HelperAssignment.class, new HelperAssignmentDatasource());
        registeredDatasources.put(PositionAggregation.class, new PositionAggregationDatasource());
        registeredDatasources.put(AggregationRelation.class, new AggregationRelationDatasource());
    }

    private static Datasources getInstance()
    {
        if (Datasources.instance == null)
        {
            Datasources.instance = new Datasources();
        }
        return Datasources.instance;
    }

    @SuppressWarnings("unchecked")
    private <T> DefaultDatasource<T> datasource(Class<T> entityClass)
    {
        DefaultDatasource<T> dataSource = registeredDatasources.get(entityClass);
        return (dataSource != null ? dataSource : defaultDatasource);
    }

    public static <T> DefaultDatasource<T> getDatasource(Class<T> entityClass)
    {
        return getInstance().datasource(entityClass);
    }
}