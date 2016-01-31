package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;

public class MissedAssignmentBuilder extends AbstractEntityBuilder<MissedAssignment>
{    
    private Helper helper;
    
    private Position position;
    
    private Event event;
    
    private Date timeStamp;

    public MissedAssignmentBuilder withHelper(Helper aHelper)
    {
        helper = aHelper;
        return this;
    }

    public MissedAssignmentBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }

    public MissedAssignmentBuilder withEvent(Event aEvent)
    {
        event = aEvent;
        return this;
    }
    
    public MissedAssignmentBuilder withTimeStamp()
    {
        timeStamp = new Date();
        return this;
    }    
    
    public MissedAssignment build()
    {
        MissedAssignment missedAssignment = new MissedAssignment();
        missedAssignment.setEvent(event);
        missedAssignment.setHelper(helper);
        missedAssignment.setPosition(position);
        missedAssignment.setTimeStamp(timeStamp);
        missedAssignment.setUsed(false);
        return missedAssignment;
    }
}