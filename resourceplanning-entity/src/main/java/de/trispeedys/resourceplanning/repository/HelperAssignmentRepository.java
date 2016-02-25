package de.trispeedys.resourceplanning.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;

public class HelperAssignmentRepository extends AbstractDatabaseRepository<HelperAssignment> implements
        DatabaseRepository<HelperAssignmentRepository>
{
    private static final String HELPER_TOO_YOUNG = "HELPER_TOO_YOUNG";

    private static final String POSITIONS_NO_SWAP = "POSITIONS_NO_SWAP";

    public List<HelperAssignment> findByEvent(Event event)
    {
        return dataSource().find(null, HelperAssignment.ATTR_EVENT, event);
    }

    public List<HelperAssignment> findUncancelledByEvent(Event event)
    {
        return dataSource().find(
                null,
                "FROM " +
                        HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event AND ec.helperAssignmentState <> '" +
                        HelperAssignmentState.CANCELLED + "'", "event", event);
    }

    protected DefaultDatasource<HelperAssignment> createDataSource()
    {
        return new HelperAssignmentDatasource();
    }

    public List<HelperAssignment> findAllHelperAssignmentsByEvent(Event event)
    {
        return dataSource().find(null, "FROM " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event", "event", event);
    }

    public List<HelperAssignment> findAllHelperAssignments(Long helperId)
    {
        return dataSource().find(null, "FROM " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId", "helperId",
                helperId);
    }

    public HelperAssignment findByHelperAndEvent(Long helperId, Long eventId)
    {
        return findByHelperAndEvent(RepositoryProvider.getRepository(HelperRepository.class).findById(helperId),
                RepositoryProvider.getRepository(EventRepository.class).findById(eventId));
    }

    public HelperAssignment findByHelperAndEvent(Helper helper, Event event)
    {
        List<HelperAssignment> result = dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
        // TODO this is a bad idea...there may be many (e.g. cancelled ones) assignments for a helper in an event
        return safeValue(result);
    }

    public HelperAssignment findByHelperAndEventAndPosition(Helper helper, Event event, Position position)
    {
        List<HelperAssignment> result =
                dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event,
                        HelperAssignment.ATTR_POSITION, position);
        return safeValue(result);
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
        List<HelperAssignment> result =
                dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_POSITION, position);
        return safeValue(result);
    }

    public HelperAssignment findByEventAndPositionId(Event event, Long positionId)
    {
        return findByEventAndPosition(event, RepositoryProvider.getRepository(PositionRepository.class).findById(positionId));
    }

    public void assignHelper(Helper helper, Event event, Position position, SessionToken sessionToken) throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, HELPER_TOO_YOUNG, helper.getLastName(),
                    helper.getFirstName(), position.getDescription()));
        }
        EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate(sessionToken);
    }

    public void confirmHelper(Helper helper, Event event, Position position, SessionToken sessionToken) throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, HELPER_TOO_YOUNG, helper.getLastName(),
                    helper.getFirstName(), position.getDescription()));
        }
        EntityFactory.buildHelperAssignment(helper, event, position, HelperAssignmentState.CONFIRMED).saveOrUpdate(sessionToken);
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
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event), HelperAssignment.ATTR_ASSIGNMENT_STATE,
                HelperAssignmentState.CANCELLED);
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CONFIRMED}.
     */
    public void confirmHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        repository.updateSingleValue(repository.findByHelperAndEvent(helper, event), HelperAssignment.ATTR_ASSIGNMENT_STATE,
                HelperAssignmentState.CONFIRMED);
    }

    public HelperAssignment getPriorAssignment(Helper helper, EventTemplate eventTemplate)
    {
        String queryString =
                "FROM " +
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

    public List<HelperAssignment> findConfirmedHelperAssignments(Event event)
    {
        return dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_ASSIGNMENT_STATE,
                HelperAssignmentState.CONFIRMED);
    }

    public void switchHelperAssignments(HelperAssignment assignmentSource, HelperAssignment assignmentTarget, Date targetDate)
    {
        // check if both helpers are suitable for the new position!!
        boolean switchSourceToTarget = assignmentSource.getHelper().isAssignableTo(assignmentTarget.getPosition(), targetDate);
        boolean switchTargetToSource = assignmentTarget.getHelper().isAssignableTo(assignmentSource.getPosition(), targetDate);
        
        if ((!(switchSourceToTarget)) || (!(switchTargetToSource)))
        {
            throw new ResourcePlanningException("unable to transfer helper assignments --> one of the helpers is not assignable to new position!!");
        }
        
        Position positionSource = assignmentSource.getPosition();
        Position positionTarget = assignmentTarget.getPosition();

        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this, null);
        try
        {
            sessionHolder.beginTransaction();
            assignmentSource.setPosition(positionTarget);
            sessionHolder.saveOrUpdate(assignmentSource);
            assignmentTarget.setPosition(positionSource);
            sessionHolder.saveOrUpdate(assignmentTarget);
            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, POSITIONS_NO_SWAP, e.getMessage()));
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }

    public void transferHelperAssignment(HelperAssignment sourceAssignment, Position targetPosition, Date targetDate)
    {
        // check if the helper is suitable for the new position!!
        if (!(sourceAssignment.getHelper().isAssignableTo(targetPosition, targetDate)))
        {
            throw new ResourcePlanningException("unable to transfer helper assignment --> helper is not assignable to new position!!");
        }
        
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this, null);
        try
        {
            sessionHolder.beginTransaction();
            sourceAssignment.setPosition(targetPosition);
            sessionHolder.saveOrUpdate(sourceAssignment);
            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, POSITIONS_NO_SWAP, e.getMessage()));
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }

    public HelperAssignment findConfirmedByPositionAndEvent(Event event, Position position)
    {
        List<HelperAssignment> list = dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_POSITION, position,
                HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CONFIRMED);
        // safe value method gives an exception if there is more than one confirmed assignment for this position in the given event
        return safeValue(list);
    }
}