package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class PositionAggregationDatasource extends DefaultDatasource<PositionAggregation>
{
    protected Class<PositionAggregation> getGenericType()
    {
        return PositionAggregation.class;
    }
}