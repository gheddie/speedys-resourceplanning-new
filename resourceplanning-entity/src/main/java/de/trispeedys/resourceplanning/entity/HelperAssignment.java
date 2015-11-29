package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;

@Entity
@Table(name = "helper_assignment")
public class HelperAssignment extends AbstractDbObject
{
    public static final String ATTR_HELPER = "helper";
    
    public static final String ATTR_EVENT = "event";

    public static final String ATTR_POSITION = "position";
    
    public static final String ATTR_ASSIGNMENT_STATE = "helperAssignmentState";
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    @Column(name = "helper_id", insertable = false, updatable = false)
    private Long helperId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "helper_assignment_state")
    private HelperAssignmentState helperAssignmentState;

    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }
    
    public Long getHelperId()
    {
        return helperId;
    }
    
    public void setHelperId(Long helperId)
    {
        this.helperId = helperId;
    }
    
    public HelperAssignmentState getHelperAssignmentState()
    {
        return helperAssignmentState;
    }
    
    public void setHelperAssignmentState(HelperAssignmentState helperAssignmentState)
    {
        this.helperAssignmentState = helperAssignmentState;
    }

    public boolean isCancelled()
    {
        return (helperAssignmentState.equals(helperAssignmentState.CANCELLED));
    }
}