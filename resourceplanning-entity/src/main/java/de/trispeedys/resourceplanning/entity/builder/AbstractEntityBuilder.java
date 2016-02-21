package de.trispeedys.resourceplanning.entity.builder;

public abstract class AbstractEntityBuilder<AbstractDbObject>
{
    public abstract AbstractDbObject build();
}