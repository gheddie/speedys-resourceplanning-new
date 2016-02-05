package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.SwapType;

public class AssignmentSwapBuilder extends AbstractEntityBuilder<AssignmentSwap>
{
    private Event event;
    
    private Position sourcePosition;
    
    private Position targetPosition;

    private SwapType swapType;

    public AssignmentSwapBuilder withEvent(Event aEvent)
    {
        this.event = aEvent;
        return this;
    }
    
    public AssignmentSwapBuilder withSourcePosition(Position aSourcePosition)
    {
        this.sourcePosition = aSourcePosition;
        return this;
    }
    
    public AssignmentSwapBuilder withTargetPosition(Position aTargetPosition)
    {
        this.targetPosition = aTargetPosition;
        return this;
    }
    
    public AssignmentSwapBuilder withSwapType(SwapType aSwapType)
    {
        this.swapType = aSwapType;
        return this;
    }
    
    public AssignmentSwap build()
    {
        AssignmentSwap assignmentSwap = new AssignmentSwap();
        assignmentSwap.setEvent(event);
        assignmentSwap.setSourcePosition(sourcePosition);
        assignmentSwap.setTargetPosition(targetPosition);
        assignmentSwap.setSwapType(swapType);
        assignmentSwap.setCreationTime(new Date());
        return assignmentSwap;
    }
}