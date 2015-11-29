package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class HelperAssignmentRepository extends AbstractDatabaseRepository<HelperAssignment> implements
        DatabaseRepository<HelperAssignmentRepository>
{
    public List<HelperAssignment> findByEvent(Event event)
    {
        return dataSource().find(HelperAssignment.ATTR_EVENT, event);
    }
    
    protected DefaultDatasource<HelperAssignment> createDataSource()
    {
        return new HelperAssignmentDatasource();
    }
    
    public List<HelperAssignment> findAllHelperAssignmentsByEvent(Event event)
    {
        return dataSource().find(
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event",
                "event", event);
    }

    public List<HelperAssignment> findAllHelperAssignments(Long helperId)
    {
        return dataSource().find(
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId",
                "helperId", helperId);
    }

    public HelperAssignment findByHelperAndEvent(Helper helper, Event event)
    {
        return dataSource().findSingle(HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
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
        return dataSource().find(HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
    }
}