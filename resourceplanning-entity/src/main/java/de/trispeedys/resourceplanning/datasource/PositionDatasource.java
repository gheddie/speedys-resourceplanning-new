package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.Position;

public class PositionDatasource extends DefaultDatasource<Position>
{
    protected Class<Position> getGenericType()
    {
        return Position.class;
    }
}