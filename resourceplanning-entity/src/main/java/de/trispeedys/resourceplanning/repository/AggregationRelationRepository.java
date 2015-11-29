package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.AggregationRelationDatasource;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class AggregationRelationRepository extends AbstractDatabaseRepository<AggregationRelation> implements DatabaseRepository<AggregationRelationRepository>
{
    protected DefaultDatasource<AggregationRelation> createDataSource()
    {
        return new AggregationRelationDatasource();
    }
}