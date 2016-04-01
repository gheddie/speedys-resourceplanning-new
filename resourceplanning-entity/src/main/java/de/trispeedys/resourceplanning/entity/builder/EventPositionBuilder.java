package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class EventPositionBuilder extends AbstractEntityBuilder<EventPosition>
{
    private Position position;
    
    private GuidedEvent event;

    public EventPositionBuilder withEvent(GuidedEvent aEvent)
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