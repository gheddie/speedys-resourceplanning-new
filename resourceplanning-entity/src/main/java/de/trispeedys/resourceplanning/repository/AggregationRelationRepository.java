package de.trispeedys.resourceplanning.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.AggregationRelationDatasource;
import de.trispeedys.resourceplanning.entity.AggregationRelation;

public class AggregationRelationRepository extends AbstractDatabaseRepository<AggregationRelation> implements DatabaseRepository<AggregationRelationRepository>
{
    protected DefaultDatasource<AggregationRelation> createDataSource()
    {
        return new AggregationRelationDatasource();
    }
}