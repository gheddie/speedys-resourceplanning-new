package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AggregationRelation;

public class AggregationRelationDatasource extends DefaultDatasource<AggregationRelation>
{
    protected Class<AggregationRelation> getGenericType()
    {
        return AggregationRelation.class;
    }
}