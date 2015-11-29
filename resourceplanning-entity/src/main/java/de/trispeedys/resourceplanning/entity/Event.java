package de.trispeedys.resourceplanning.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

@Entity
@Table(name = "event")
public class Event extends AbstractDbObject implements HierarchicalEventItem
{
    public static final String ATTR_EVENT_STATE = "eventState";
    
    public static final String ATTR_EVENT_KEY = "eventKey";
    
    @Temporal(TemporalType.DATE)
    @Column(name = "event_date")
    private Date eventDate;
    
    @Column(name = "event_key")
    @NotNull
    private String eventKey;
    
    @NotNull
    private String description;
    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "event_template_id")
    private EventTemplate eventTemplate;
    
    @OneToOne
    @JoinColumn(name = "parent_event_id")
    private Event parentEvent;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private List<EventPosition> eventPositions;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    @NotNull
    private EventState eventState;

    public Date getEventDate()
    {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getEventKey()
    {
        return eventKey;
    }

    public void setEventKey(String eventKey)
    {
        this.eventKey = eventKey;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public List<EventPosition> getEventPositions()
    {
        return eventPositions;
    }
    
    public void setEventPositions(List<EventPosition> eventPositions)
    {
        this.eventPositions = eventPositions;
    }
    
    public EventState getEventState()
    {
        return eventState;
    }
    
    public void setEventState(EventState eventState)
    {
        this.eventState = eventState;
    }
    
    public EventTemplate getEventTemplate()
    {
        return eventTemplate;
    }
    
    public void setEventTemplate(EventTemplate eventTemplate)
    {
        this.eventTemplate = eventTemplate;
    }
    
    public Event getParentEvent()
    {
        return parentEvent;
    }
    
    public void setParentEvent(Event parentEvent)
    {
        this.parentEvent = parentEvent;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+description+", "+eventKey+"]";
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_EVENT;
    }
    
    public String getDifferentiator()
    {
        return "[E]";
    }
}