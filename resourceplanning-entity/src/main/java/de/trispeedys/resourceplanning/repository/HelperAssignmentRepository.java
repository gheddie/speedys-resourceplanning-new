package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

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
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event", "event", event);
    }

    public List<HelperAssignment> findAllHelperAssignments(Long helperId)
    {
        return dataSource().find(null,
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId", "helperId",
                helperId);
    }

    public HelperAssignment findByHelperAndEvent(Helper helper, Event event)
    {
        List<HelperAssignment> result =
                dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
        if (result == null)
        {
            return null;
        }
        else
        {
            return (result.size() > 0
                    ? result.get(0)
                    : null);
        }
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

    public HelperAssignment findByEventAndPosition(Event event, Position position)
    {
        List<HelperAssignment> result = dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_POSITION, position);
        return (result == null || result.size() == 0 ? null : result.get(0));
    }

    public HelperAssignment findByEventAndPositionId(Event event, Long positionId)
    {
        return findByEventAndPosition(event,
                RepositoryProvider.getRepository(PositionRepository.class).findById(positionId));
    }

    public void assignHelper(Helper helper, Event event, Position position) throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException("helper is to young for this position!");
        }
        EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate();
    }

    public boolean isFirstAssignment(Long helperId)
    {
        List<HelperAssignment> helperAssignments =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).findAllHelperAssignments(helperId);
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CANCELLED}.
     */
    public void cancelHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event),
                HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CANCELLED);
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CONFIRMED}.
     */
    public void confirmHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event),
                HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CONFIRMED);
    }

    public HelperAssignment getPriorAssignment(Helper helper, EventTemplate eventTemplate)
    {
        String queryString =
                "From " +
                        HelperAssignment.class.getSimpleName() +
                        " ha INNER JOIN ha.event ev WHERE ha.helperId = :helperId AND ev.eventTemplate = :eventTemplate ORDER BY ev.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helper.getId());
        parameters.put("eventTemplate", eventTemplate);
        List<Object[]> list = Datasources.getDatasource(HelperAssignment.class).find(null, queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        return (HelperAssignment) list.get(0)[0];
    }
}