package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionBuilder extends AbstractEntityBuilder<Position>
{
    private String description;
    
    private int minimalAge;

    private Domain domain;

    private int positionNumber;

    private boolean choosable;

    private Integer assignmentPriority;
    
    public PositionBuilder withDomain(Domain aDomain)
    {
        domain = aDomain;
        return this;
    }

    public PositionBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }
    
    public PositionBuilder withMinimalAge(int aMinimalAge)
    {
        minimalAge = aMinimalAge;
        return this;
    }
    
    public PositionBuilder withPositionNumber(int aPositionNumber)
    {
        positionNumber = aPositionNumber;
        return this;
    }
    
    public PositionBuilder withChoosable(boolean aChoosable)
    {
        choosable = aChoosable;
        return this;
    }
    
    public PositionBuilder withAssignmentPriority(Integer aAssignmentPriority)
    {
        assignmentPriority = aAssignmentPriority;
        return this;
    }
    
    public Position build()
    {
        Position position = new Position();
        position.setDescription(description);
        position.setMinimalAge(minimalAge);
        position.setDomain(domain);
        position.setAuthorityOverride(false);
        position.setPositionNumber(positionNumber);
        position.setChoosable(choosable);
        position.setAssignmentPriority(assignmentPriority);
        return position;
    }
}