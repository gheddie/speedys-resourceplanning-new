package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.ManualAssignmentCommentDatasource;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;

public class ManualAssignmentCommentRepository extends AbstractDatabaseRepository<ManualAssignmentComment> implements DatabaseRepository<ManualAssignmentCommentRepository>
{
    protected DefaultDatasource<ManualAssignmentComment> createDataSource()
    {
        return new ManualAssignmentCommentDatasource();
    }

    public ManualAssignmentComment findByEventAndHelper(GuidedEvent event, Helper helper)
    {
        List<ManualAssignmentComment> list = dataSource().find(null, ManualAssignmentComment.ATTR_EVENT, event, ManualAssignmentComment.ATTR_HELPER, helper);
        return safeValue(list);
    }
}