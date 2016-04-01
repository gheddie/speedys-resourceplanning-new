package de.trispeedys.resourceplanning.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.SimpleEventDatasource;
import de.trispeedys.resourceplanning.entity.SimpleEvent;

public class SimpleEventRepository extends AbstractDatabaseRepository<SimpleEvent> implements DatabaseRepository<SimpleEventRepository>
{
    protected DefaultDatasource<SimpleEvent> createDataSource()
    {
        return new SimpleEventDatasource();
    }
}