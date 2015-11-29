package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "position_aggregation")
public class PositionAggregation extends AbstractDbObject
{
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
}