package de.trispeedys.resourceplanning.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.AppConfigurationEntryDatasource;
import de.trispeedys.resourceplanning.entity.AppConfigurationEntry;

public class AppConfigurationEntryRepository extends AbstractDatabaseRepository<AppConfigurationEntry> implements DatabaseRepository<AppConfigurationEntryRepository>
{
    protected DefaultDatasource<AppConfigurationEntry> createDataSource()
    {
        return new AppConfigurationEntryDatasource();
    }
}