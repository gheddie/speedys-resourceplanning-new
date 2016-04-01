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

import org.hibernate.validator.constraints.Length;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

@Entity
@Table(name = "guided_event")
public class GuidedEvent extends SimpleEvent implements HierarchicalEventItem
{
    public static final String ATTR_EVENT_STATE = "eventState";
    
    public static final String ATTR_EVENT_KEY = "eventKey";
    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "event_template_id")
    private EventTemplate eventTemplate;
    
    @OneToOne
    @JoinColumn(name = "parent_event_id")
    private GuidedEvent parentEvent;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private List<EventPosition> eventPositions;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    @NotNull
    private EventState eventState;

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
    
    public GuidedEvent getParentEvent()
    {
        return parentEvent;
    }
    
    public void setParentEvent(GuidedEvent parentEvent)
    {
        this.parentEvent = parentEvent;
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_EVENT;
    }
    
    public String getDifferentiator()
    {
        return "[E]";
    }

    public boolean isFinished()
    {
        return (eventState.equals(EventState.FINISHED));
    }
}