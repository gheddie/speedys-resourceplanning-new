package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.GuidedEventDatasource;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.misc.EventState;

public class GuidedEventRepository extends AbstractDatabaseRepository<GuidedEvent> implements DatabaseRepository<GuidedEventRepository>
{
    public List<GuidedEvent> findEventByTemplateOrdered(String description)
    {
        String queryString =
                "From " +
                        GuidedEvent.class.getSimpleName() +
                        " ev INNER JOIN ev.eventTemplate et WHERE et.description = :description ORDER BY ev.eventDate ASC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("description", description);
        List<Object[]> list = dataSource().find(null, queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        List<GuidedEvent> result = new ArrayList<GuidedEvent>();
        for (Object[] obj : list)
        {
            result.add((GuidedEvent) obj[0]);
        }
        return result;
    }

    public GuidedEvent findEventByEventKey(String eventKey)
    {
        return dataSource().findSingle(null, GuidedEvent.ATTR_EVENT_KEY, eventKey);
    }
    
    public List<GuidedEvent> findEventsByTemplateAndStatus(String templateName, EventState eventState)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(GuidedEvent.ATTR_EVENT_STATE, eventState);
        parameters.put(EventTemplate.ATTR_DESCRIPTION, templateName);
        List<Object[]> list =
                Datasources.getDatasource(EventTemplate.class)
                        .find(null, "FROM " +
                                GuidedEvent.class.getSimpleName() +
                                " ev INNER JOIN ev.eventTemplate et WHERE ev.eventState = :eventState AND et.description = :description", parameters);
        List<GuidedEvent> result = new ArrayList<GuidedEvent>();
        for (Object[] o : list)
        {
            result.add((GuidedEvent) o[0]);
        }
        return result;
    }

    protected DefaultDatasource<GuidedEvent> createDataSource()
    {
        return new GuidedEventDatasource();
    }

    public void updateEventState(GuidedEvent event, EventState eventState)
    {
        if (event == null)
        {
            return;
        }
        event.setEventState(eventState);
        event.saveOrUpdate();
    }

    public List<GuidedEvent> findInitiatedEvents()
    {
        return dataSource().find(null, GuidedEvent.ATTR_EVENT_STATE, EventState.INITIATED);
    }
}