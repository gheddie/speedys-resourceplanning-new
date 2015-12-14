package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.EventPositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class EventPositionRepository extends AbstractDatabaseRepository<EventPosition> implements DatabaseRepository<EventPositionRepository>
{
    public EventPosition findByEventAndPositionNumber(Event event, int positionNumber)
    {
        return (EventPosition) dataSource().findSingle(null, EventPosition.ATTR_EVENT, event,
                EventPosition.ATTR_POSITION, RepositoryProvider.getRepository(PositionRepository.class)
                        .findPositionByPositionNumber(positionNumber));
    }

    protected DefaultDatasource<EventPosition> createDataSource()
    {
        return new EventPositionDatasource();
    }

    public List<EventPosition> findByEvent(Event event)
    {
        return dataSource().find(null, EventPosition.ATTR_EVENT, event);
    }
}