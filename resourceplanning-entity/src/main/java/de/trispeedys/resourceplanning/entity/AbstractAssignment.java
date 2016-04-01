package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

@MappedSuperclass
public abstract class AbstractAssignment extends AbstractDbObject
{
    public static final String ATTR_HELPER = "helper";
    
    public static final String ATTR_EVENT = "event";

    public static final String ATTR_POSITION = "position";
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    @Column(name = "helper_id", insertable = false, updatable = false)
    private Long helperId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private GuidedEvent event;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;
    
    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }

    public GuidedEvent getEvent()
    {
        return event;
    }

    public void setEvent(GuidedEvent event)
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
}