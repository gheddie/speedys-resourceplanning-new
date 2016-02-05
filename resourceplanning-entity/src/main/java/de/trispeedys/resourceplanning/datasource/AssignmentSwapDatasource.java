package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;

public class AssignmentSwapDatasource extends DefaultDatasource<AssignmentSwap>
{
    protected Class<AssignmentSwap> getGenericType()
    {
        return AssignmentSwap.class;
    }
}