package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.EventPosition;

public class EventPositionDatasource extends DefaultDatasource<EventPosition>
{
    protected Class<EventPosition> getGenericType()
    {
        return EventPosition.class;
    }
}