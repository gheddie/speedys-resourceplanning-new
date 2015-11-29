package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;

public abstract class AbstractMailTemplate<T>
{
    private Helper helper;

    private Event event;

    private Position position;
    
    public AbstractMailTemplate(Helper helper, Event event, Position position)
    {
        super();
        this.helper = helper;
        this.event = event;
        this.position = position;
    }
    
    public Helper getHelper()
    {
        return helper;
    }
    
    public Event getEvent()
    {
        return event;
    }
    
    public Position getPosition()
    {
        return position;
    }

    public abstract String constructBody();
    
    public abstract String constructSubject();
    
    public abstract MessagingFormat getMessagingFormat();

    public abstract MessagingType getMessagingType();
}