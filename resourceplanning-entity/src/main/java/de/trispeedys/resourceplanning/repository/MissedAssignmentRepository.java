package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.MissedAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.MissedAssignmentBuilder;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class MissedAssignmentRepository extends AbstractDatabaseRepository<MissedAssignment> implements
        DatabaseRepository<MissedAssignmentRepository>
{
    protected DefaultDatasource<MissedAssignment> createDataSource()
    {
        return new MissedAssignmentDatasource();
    }

    public void createMissedAssignment(Long eventId, Long helperId, Long positionId)
    {
        new MissedAssignmentBuilder().withPosition(
                RepositoryProvider.getRepository(PositionRepository.class).findById(positionId))
                .withHelper(RepositoryProvider.getRepository(HelperRepository.class).findById(helperId))
                .withEvent(RepositoryProvider.getRepository(EventRepository.class).findById(eventId))
                .withTimeStamp()
                .build()
                .saveOrUpdate();
    }

    public List<MissedAssignment> findByPositionAndEvent(Long positionId, Long eventId)
    {
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MissedAssignment.ATTR_POSITION, position);
        parameters.put(MissedAssignment.ATTR_EVENT, event);
        return dataSource().find(
                null,
                "FROM " +
                        MissedAssignment.class.getSimpleName() +
                        " ma WHERE ma.position = :position AND ma.event = :event", parameters);
    }
}