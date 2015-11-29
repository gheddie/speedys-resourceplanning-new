package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.HelperHistory;

public class HelperHistoryDatasource extends DefaultDatasource<HelperHistory>
{
    protected Class<HelperHistory> getGenericType()
    {
        return HelperHistory.class;
    }
}