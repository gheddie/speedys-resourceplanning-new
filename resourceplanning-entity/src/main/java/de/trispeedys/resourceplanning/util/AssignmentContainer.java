package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class AssignmentContainer
{
    private Position position;
    
    private Helper helper;

    public AssignmentContainer(Position position, Helper helper)
    {
        super();
        this.position = position;
        this.helper = helper;
    }
    
    public Position getPosition()
    {
        return position;
    }
    
    public Helper getHelper()
    {
        return helper;
    }
}