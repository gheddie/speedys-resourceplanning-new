package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class AggregationRelationBuilder extends AbstractEntityBuilder<AggregationRelation>
{
    private Position position;
    
    private PositionAggregation positionAggregation;
    
    public AggregationRelationBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }
    
    public AggregationRelationBuilder withPositionAggregation(PositionAggregation aPositionAggregation)
    {
        positionAggregation = aPositionAggregation;
        return this;
    }

    public AggregationRelation build()
    {
        AggregationRelation relation = new AggregationRelation();
        relation.setPosition(position);
        relation.setPositionAggregation(positionAggregation);
        return relation;
    }
}