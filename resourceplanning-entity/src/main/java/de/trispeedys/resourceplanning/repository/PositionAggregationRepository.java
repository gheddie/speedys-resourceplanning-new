package de.trispeedys.resourceplanning.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.PositionAggregationDatasource;
import de.trispeedys.resourceplanning.entity.PositionAggregation;

public class PositionAggregationRepository extends AbstractDatabaseRepository<PositionAggregation> implements DatabaseRepository<PositionAggregationRepository>
{
    protected DefaultDatasource<PositionAggregation> createDataSource()
    {
        return new PositionAggregationDatasource();
    }
}