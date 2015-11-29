package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;

public class HelperAssignmentBuilder extends AbstractEntityBuilder<HelperAssignment>
{
    private Helper helper;
    
    private Position position;

    private Event event;

    private HelperAssignmentState helperAssignmentState;

    public HelperAssignmentBuilder withHelper(Helper aHelper)
    {
        helper = aHelper;
        return this;
    }

    public HelperAssignmentBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }

    public HelperAssignmentBuilder withEvent(Event aEvent)
    {
        event = aEvent;
        return this;
    }
    
    public HelperAssignmentBuilder withHelperAssignmentState(HelperAssignmentState aHelperAssignmentState)
    {
        helperAssignmentState = aHelperAssignmentState;
        return this;
    }
    
    public HelperAssignment build()
    {
        HelperAssignment helperAssignment = new HelperAssignment();
        helperAssignment.setEvent(event);
        helperAssignment.setHelper(helper);
        helperAssignment.setPosition(position);
        helperAssignment.setHelperAssignmentState(helperAssignmentState);
        return helperAssignment;
    }
}