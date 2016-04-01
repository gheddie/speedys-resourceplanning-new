package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.SimpleEvent;

public class SimpleEventDatasource extends DefaultDatasource<SimpleEvent>
{
    protected Class<SimpleEvent> getGenericType()
    {
        return SimpleEvent.class;
    }
}