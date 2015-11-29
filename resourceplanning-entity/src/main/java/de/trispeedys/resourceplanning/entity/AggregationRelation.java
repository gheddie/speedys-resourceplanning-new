package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "aggregation_relation")
public class AggregationRelation extends AbstractDbObject
{
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id")
    private Position position;
    
    @Column(name="position_id", insertable=false, updatable=false)
    private Long positionId;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_aggregation_id")
    private PositionAggregation positionAggregation;
    
    public Position getPosition()
    {
        return position;
    }
    
    public void setPosition(Position position)
    {
        this.position = position;
    }
    
    public PositionAggregation getPositionAggregation()
    {
        return positionAggregation;
    }
    
    public void setPositionAggregation(PositionAggregation positionAggregation)
    {
        this.positionAggregation = positionAggregation;
    }
    
    public Long getPositionId()
    {
        return positionId;
    }
}