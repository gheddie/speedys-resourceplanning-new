package de.trispeedys.resourceplanning.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;

public class ManualAssignmentCommentDatasource extends DefaultDatasource<ManualAssignmentComment>
{
    protected Class<ManualAssignmentComment> getGenericType()
    {
        return ManualAssignmentComment.class;
    }
}