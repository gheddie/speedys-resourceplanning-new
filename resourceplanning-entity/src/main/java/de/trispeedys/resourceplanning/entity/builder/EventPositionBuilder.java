package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class EventPositionBuilder extends AbstractEntityBuilder<EventPosition>
{
    private Position position;
    
    private Event event;

    public EventPositionBuilder withEvent(Event aEvent)
    {
        event = aEvent;
        return this;
    }

    public EventPositionBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }
    
    public EventPosition build()
    {
        EventPosition eventPosition = new EventPosition();
        eventPosition.setPosition(position);
        eventPosition.setEvent(event);
        return eventPosition;
    }
}