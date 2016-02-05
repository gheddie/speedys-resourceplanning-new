package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.SwapResult;
import de.trispeedys.resourceplanning.entity.misc.SwapType;

@Entity
@Table(name = "assignment_swap")
public class AssignmentSwap extends AbstractDbObject
{
    public static final String ATTR_SWAP_TYPE = "swapType";
    
    public static final String ATTR_SWAP_RESULT = "swapResult";
    
    public static final String ATTR_SOURCE_POSITION = "sourcePosition";
    
    public static final String ATTR_TARGET_POSITION = "targetPosition";
    
    public static final String ATTR_EVENT = "event";    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "swap_type")
    private SwapType swapType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "swap_result")
    private SwapResult swapResult;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_position_id")
    private Position sourcePosition;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_position_id")
    private Position targetPosition;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;
 
    public SwapType getSwapType()
    {
        return swapType;
    }
    
    public void setSwapType(SwapType swapType)
    {
        this.swapType = swapType;
    }
    
    public SwapResult getSwapResult()
    {
        return swapResult;
    }
    
    public void setSwapResult(SwapResult swapResult)
    {
        this.swapResult = swapResult;
    }

    public Position getSourcePosition()
    {
        return sourcePosition;
    }

    public void setSourcePosition(Position sourcePosition)
    {
        this.sourcePosition = sourcePosition;
    }

    public Position getTargetPosition()
    {
        return targetPosition;
    }

    public void setTargetPosition(Position targetPosition)
    {
        this.targetPosition = targetPosition;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }
    
    public Date getCreationTime()
    {
        return creationTime;
    }
    
    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }
}