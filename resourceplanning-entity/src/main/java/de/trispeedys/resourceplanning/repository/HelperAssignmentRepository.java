package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class HelperAssignmentRepository extends AbstractDatabaseRepository<HelperAssignment> implements
        DatabaseRepository<HelperAssignmentRepository>
{
    public List<HelperAssignment> findByEvent(Event event)
    {
        return dataSource().find(null, HelperAssignment.ATTR_EVENT, event);
    }
    
    protected DefaultDatasource<HelperAssignment> createDataSource()
    {
        return new HelperAssignmentDatasource();
    }
    
    public List<HelperAssignment> findAllHelperAssignmentsByEvent(Event event)
    {
        return dataSource().find(null, 
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event",
                "event", event);
    }

    public List<HelperAssignment> findAllHelperAssignments(Long helperId)
    {
        return dataSource().find(null, 
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId",
                "helperId", helperId);
    }

    public HelperAssignment findByHelperAndEvent(Helper helper, Event event)
    {
        return dataSource().findSingle(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
    }

    /**
     * gets the {@link HelperAssignment} for the given {@link Helper} in the given year (can be more than one, as one
     * helper can be assigned to multiple positions in one event).
     * 
     * @param helper
     * @param event
     * @return
     */
    public List<HelperAssignment> getHelperAssignments(Helper helper, Event event)
    {
        return dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
    }

    public List<HelperAssignment> findByEventAndPosition(Event event, Position position)
    {
        return dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_POSITION, position);
    }

    public List<HelperAssignment> findByEventAndPositionId(Event event, Long positionId)
    {
        return findByEventAndPosition(event, RepositoryProvider.getRepository(PositionRepository.class).findById(positionId));
    }
}