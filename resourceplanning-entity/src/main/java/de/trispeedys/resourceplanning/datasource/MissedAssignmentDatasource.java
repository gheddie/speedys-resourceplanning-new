package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.MissedAssignment;

public class MissedAssignmentDatasource extends DefaultDatasource<MissedAssignment>
{
    protected Class<MissedAssignment> getGenericType()
    {
        return MissedAssignment.class;
    }
}