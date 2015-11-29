package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Realtion betweeb {@link Event} and {@link Position}.
 * 
 * @author Stefan.Schulz
 *
 */
@Entity
@Table(name = "event_position")
public class EventPosition extends AbstractDbObject
{
    public static final String ATTR_EVENT = "event";

    public static final String ATTR_POSITION = "position";

    @OneToOne
    @JoinColumn(name = "position_id")
    private Position position;
    
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    } 
}