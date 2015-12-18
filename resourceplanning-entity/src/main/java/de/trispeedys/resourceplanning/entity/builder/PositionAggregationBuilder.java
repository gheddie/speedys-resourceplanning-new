package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class PositionAggregationBuilder extends AbstractEntityBuilder<PositionAggregation>
{
    private String name;
    
    private boolean active;

    public PositionAggregationBuilder withName(String aName)
    {
        name = aName;
        return this;
    }
    
    public PositionAggregationBuilder withActive(boolean anActive)
    {
        active = anActive;
        return this;
    }
    
    public PositionAggregation build()
    {
        PositionAggregation positionAggregation = new PositionAggregation();
        positionAggregation.setName(name);
        positionAggregation.setActive(active);
        return positionAggregation;
    }
}