package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.Helper;

public class HelperDatasource extends DefaultDatasource<Helper>
{
    protected Class<Helper> getGenericType()
    {
        return Helper.class;
    }
}