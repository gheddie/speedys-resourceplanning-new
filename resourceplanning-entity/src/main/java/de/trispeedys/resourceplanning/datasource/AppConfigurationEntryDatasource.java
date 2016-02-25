package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AppConfigurationEntry;

public class AppConfigurationEntryDatasource extends DefaultDatasource<AppConfigurationEntry>
{
    protected Class<AppConfigurationEntry> getGenericType()
    {
        return AppConfigurationEntry.class;
    }
}