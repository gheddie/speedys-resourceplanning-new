package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.EventPositionDatasource;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class EventPositionRepository extends AbstractDatabaseRepository<EventPosition> implements DatabaseRepository<EventPositionRepository>
{
    public EventPosition findByEventAndPositionNumber(GuidedEvent event, Position position)
    {
        List<EventPosition> result = dataSource().find(null, EventPosition.ATTR_EVENT, event,
                EventPosition.ATTR_POSITION, position);
        return safeValue(result);
    }

    protected DefaultDatasource<EventPosition> createDataSource()
    {
        return new EventPositionDatasource();
    }

    public List<EventPosition> findByEvent(GuidedEvent event)
    {
        return dataSource().find(null, EventPosition.ATTR_EVENT, event);
    }
}