package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.GuidedEvent;

public class GuidedEventDatasource extends DefaultDatasource<GuidedEvent>
{
    protected Class<GuidedEvent> getGenericType()
    {
        return GuidedEvent.class;
    }
}