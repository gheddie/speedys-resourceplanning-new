package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.EventDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class EventRepository extends AbstractDatabaseRepository<Event> implements DatabaseRepository<EventRepository>
{
    public List<Event> findEventByTemplateOrdered(String eventTemplateName)
    {
        String queryString =
                "From " +
                        Event.class.getSimpleName() +
                        " ev INNER JOIN ev.eventTemplate et WHERE et.description = :description ORDER BY ev.eventDate ASC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("description", eventTemplateName);
        List<Object[]> list = dataSource().find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        List<Event> result = new ArrayList<Event>();
        for (Object[] obj : list)
        {
            result.add((Event) obj[0]);
        }
        return result;
    }

    public Event findEventByEventKey(String eventKey)
    {
        return dataSource().findSingle(Event.ATTR_EVENT_KEY, eventKey);
    }
    
    public List<Event> findEventsByTemplateAndStatus(String templateName, EventState eventState)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(Event.ATTR_EVENT_STATE, eventState);
        parameters.put(EventTemplate.ATTR_DESCRIPTION, templateName);
        List<Object[]> list =
                Datasources.getDatasource(EventTemplate.class)
                        .find("FROM " +
                                Event.class.getSimpleName() +
                                " ev INNER JOIN ev.eventTemplate et WHERE ev.eventState = :eventState AND et.description = :description", parameters);
        List<Event> result = new ArrayList<Event>();
        for (Object[] o : list)
        {
            result.add((Event) o[0]);
        }
        return result;
    }

    protected DefaultDatasource<Event> createDataSource()
    {
        return new EventDatasource();
    }

    public void updateEventState(Event event, EventState eventState)
    {
        if (event == null)
        {
            return;
        }
        event.setEventState(eventState);
        event.saveOrUpdate();
    }

    public List<Event> findInitiatedEvents()
    {
        return dataSource().find(Event.ATTR_EVENT_STATE, EventState.INITIATED);
    }
}