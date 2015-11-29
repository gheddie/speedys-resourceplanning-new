package de.trispeedys.resourceplanning.repository.base;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.PositionAggregationDatasource;
import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class PositionAggregationRepository extends AbstractDatabaseRepository<PositionAggregation> implements DatabaseRepository<PositionAggregationRepository>
{
    protected DefaultDatasource<PositionAggregation> createDataSource()
    {
        return new PositionAggregationDatasource();
    }
}