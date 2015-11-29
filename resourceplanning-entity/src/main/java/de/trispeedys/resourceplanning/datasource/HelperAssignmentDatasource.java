package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.HelperAssignment;

public class HelperAssignmentDatasource extends DefaultDatasource<HelperAssignment>
{
    protected Class<HelperAssignment> getGenericType()
    {
        return HelperAssignment.class;
    }
}