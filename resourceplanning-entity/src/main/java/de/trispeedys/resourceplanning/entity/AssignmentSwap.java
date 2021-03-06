package de.trispeedys.resourceplanning.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
import de.trispeedys.resourceplanning.entity.misc.SwapType;

@Entity
@Table(name = "assignment_swap")
public class AssignmentSwap extends AbstractDbObject
{
    public static final String ATTR_SWAP_TYPE = "swapType";

    public static final String ATTR_SWAP_STATE = "swapState";

    public static final String ATTR_SOURCE_POSITION = "sourcePosition";

    public static final String ATTR_TARGET_POSITION = "targetPosition";

    public static final String ATTR_EVENT = "event";

    @Enumerated(EnumType.STRING)
    @Column(name = "swap_type")
    private SwapType swapType;

    @Enumerated(EnumType.STRING)
    @Column(name = "swap_state")
    private SwapState swapState;

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
    @JoinColumn(name = "source_helper_id")
    private Helper sourceHelper;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_helper_id")
    private Helper targetHelper;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private GuidedEvent event;

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

    public SwapState getSwapState()
    {
        return swapState;
    }

    public void setSwapState(SwapState swapState)
    {
        this.swapState = swapState;
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

    public GuidedEvent getEvent()
    {
        return event;
    }

    public void setEvent(GuidedEvent event)
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

    public Helper getSourceHelper()
    {
        return sourceHelper;
    }

    public void setSourceHelper(Helper sourceHelper)
    {
        this.sourceHelper = sourceHelper;
    }

    public Helper getTargetHelper()
    {
        return targetHelper;
    }

    public void setTargetHelper(Helper targetHelper)
    {
        this.targetHelper = targetHelper;
    }

    public boolean collidesWith(AssignmentSwap swap)
    {
        List<Long> affectedPositions = new ArrayList<Long>();
        affectedPositions.add(sourcePosition.getId());
        affectedPositions.add(targetPosition.getId());
        // TODO implement + use is to make there are no active swaps for the same position at the same time
        return ((affectedPositions.contains(swap.getSourcePosition().getId())) || (affectedPositions.contains(swap.getTargetPosition()
                .getId())));
    }

    /*
    public Object[] getNotificationSuccessParameters(boolean inverted)
    {
        if (inverted)
        {
            // target position and domain, then source position and domain
            return new Object[]
            {
                    targetPosition.getDescription(),
                    targetPosition.getDomain().getName(),
                    sourcePosition.getDescription(),
                    sourcePosition.getDomain().getName()

            };
        }
        else
        {
            // source position and domain, then target position and domain
            return new Object[]
            {
                    sourcePosition.getDescription(),
                    sourcePosition.getDomain().getName(),
                    targetPosition.getDescription(),
                    targetPosition.getDomain().getName()
            };
        }
    }
    */

    /*
    public Object[] getNotificationFailureParameters(boolean inverted)
    {
        if (inverted)
        {
            // source position and domain
            return new Object[]
            {
                    sourcePosition.getDescription(), sourcePosition.getDomain().getName()
            };   
        }
        else
        {
            // target position and domain
            return new Object[]
            {
                    targetPosition.getDescription(), targetPosition.getDomain().getName()
            };
        }
    }
    */
}