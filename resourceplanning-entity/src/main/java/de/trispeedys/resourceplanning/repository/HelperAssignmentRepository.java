package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.HelperAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

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
                        HelperAssignment.class.getSimpleName() +
                        " ec WHERE ec.event = :event AND ec.helperAssignmentState <> '" +
                        HelperAssignmentState.CANCELLED + "'", "event", event);
    }

    protected DefaultDatasource<HelperAssignment> createDataSource()
    {
        return new HelperAssignmentDatasource();
    }

    public List<HelperAssignment> findAllHelperAssignmentsByEvent(Event event)
    {
        return dataSource().find(null,
                "FROM " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event", "event", event);
    }

    public List<HelperAssignment> findAllHelperAssignments(Long helperId)
    {
        return dataSource().find(null,
                "FROM " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId", "helperId",
                helperId);
    }

    public HelperAssignment findByHelperAndEvent(Long helperId, Long eventId)
    {
        return findByHelperAndEvent(RepositoryProvider.getRepository(HelperRepository.class).findById(helperId),
                RepositoryProvider.getRepository(EventRepository.class).findById(eventId));
    }

    public HelperAssignment findByHelperAndEvent(Helper helper, Event event)
    {
        List<HelperAssignment> result =
                dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
        return safeValue(result);
    }
    
    public HelperAssignment findByHelperAndEventAndPosition(Helper helper, Event event, Position position)
    {
        List<HelperAssignment> result =
                dataSource().find(null, HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_POSITION, position);
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
        return findByEventAndPosition(event,
                RepositoryProvider.getRepository(PositionRepository.class).findById(positionId));
    }

    public void assignHelper(Helper helper, Event event, Position position, SessionToken sessionToken) throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, HELPER_TOO_YOUNG,
                    helper.getLastName(), helper.getFirstName(), position.getDescription()));
        }
        EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate(sessionToken);
    }
    
    public void confirmHelper(Helper helper, Event event, Position position, SessionToken sessionToken) throws ResourcePlanningException
    {
        if (!(helper.isAssignableTo(position, event.getEventDate())))
        {
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, HELPER_TOO_YOUNG,
                    helper.getLastName(), helper.getFirstName(), position.getDescription()));
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
        return dataSource().find(null, HelperAssignment.ATTR_EVENT, event, HelperAssignment.ATTR_ASSIGNMENT_STATE, HelperAssignmentState.CONFIRMED);
    }
    
    public void switchHelperAssignments(HelperAssignment assignmentSource, HelperAssignment assignmentTarget)
    {
        Position positionSource = assignmentSource.getPosition();
        Position positionTarget = assignmentTarget.getPosition();
        
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this);
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

    public void transferHelperAssignment(HelperAssignment sourceAssignment, Position targetPosition)
    {
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this);
        try
        {
            sessionHolder.beginTransaction();

            Event event = sourceAssignment.getEvent();
            Helper helper = sourceAssignment.getHelper();
            HelperAssignmentState assignmentState = sourceAssignment.getHelperAssignmentState();
            
            // (1) cancel source
            sourceAssignment.setHelperAssignmentState(HelperAssignmentState.CANCELLED);
            sessionHolder.saveOrUpdate(sourceAssignment);
            
            // TODO why do i need a flush here?
            sessionHolder.flush();
                    
            // (2) create new assigment with target
            switch(assignmentState)
            {
                case PLANNED:
                    assignHelper(helper, event, targetPosition, sessionHolder.getToken());
                    break;
                case CONFIRMED:
                    confirmHelper(helper, event, targetPosition, sessionHolder.getToken());
                    break;
            }
            
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
}