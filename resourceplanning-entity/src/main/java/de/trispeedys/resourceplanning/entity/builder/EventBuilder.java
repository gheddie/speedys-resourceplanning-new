package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.misc.EventState;

public class EventBuilder extends AbstractEntityBuilder<Event>
{
    private String description;
    
    private Date eventDate;
    
    private String eventKey;

    private EventState eventState;

    private EventTemplate eventTemplate;

    private Event parentEvent;

    public EventBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }
    
    public EventBuilder withDate(Date aEventDate)
    {
        eventDate = aEventDate;
        return this;
    }
    
    public EventBuilder withEventKey(String aEventKey)
    {
        eventKey = aEventKey;
        return this;
    }
    
    public EventBuilder withEventState(EventState aEventState)
    {
        eventState = aEventState;
        return this;
    }
    
    public EventBuilder withEventTemplate(EventTemplate aEventTemplate)
    {
        eventTemplate = aEventTemplate;
        return this;
    }
    
    public EventBuilder withParentEvent(Event aParentEvent)
    {
        parentEvent = aParentEvent;
        return this;
    }
    
    public Event build()
    {
        Event event = new Event();
        event.setDescription(description);
        event.setEventKey(eventKey);
        event.setEventDate(eventDate);
        event.setEventState(eventState);
        event.setEventTemplate(eventTemplate);
        event.setParentEvent(parentEvent);
        return event;
    }
}