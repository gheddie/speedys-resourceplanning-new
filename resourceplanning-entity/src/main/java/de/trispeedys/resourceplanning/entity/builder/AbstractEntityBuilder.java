package de.trispeedys.resourceplanning.entity.builder;

public abstract class AbstractEntityBuilder<AbstractDbObject>
{
    private AbstractDbObject entity;
    
    public abstract AbstractDbObject build();
}