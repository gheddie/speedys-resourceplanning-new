package de.trispeedys.resourceplanning.repository;

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
        return (EventPosition) dataSource().findSingle(EventPosition.ATTR_EVENT, event,
                EventPosition.ATTR_POSITION, RepositoryProvider.getRepository(PositionRepository.class)
                        .findPositionByPositionNumber(positionNumber));
    }

    protected DefaultDatasource<EventPosition> createDataSource()
    {
        return new EventPositionDatasource();
    }
}