package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.EventTemplate;

public class EventTemplateDatasource extends DefaultDatasource<EventTemplate>
{
    protected Class<EventTemplate> getGenericType()
    {
        return EventTemplate.class;
    }
}