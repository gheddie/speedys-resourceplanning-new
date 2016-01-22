package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.MissedAssignmentDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.MissedAssignmentBuilder;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class MissedAssignmentRepository extends AbstractDatabaseRepository<MissedAssignment> implements DatabaseRepository<MissedAssignmentRepository>
{
    protected DefaultDatasource<MissedAssignment> createDataSource()
    {
        return new MissedAssignmentDatasource();
    }

    /**
     * Creates a {@link MissedAssignment}. Doing so, the method first removes all prior objects for the helper and the
     * event. This makes sure that at one point of time there is only missed assignment persisted in the particular
     * event for the given {@link Helper}.
     * 
     * @param eventId
     * @param helperId
     * @param positionId
     */
    public void createMissedAssignmentExclusive(Long eventId, Long helperId, Long positionId)
    {
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this);
        try
        {
            sessionHolder.beginTransaction();

            SessionToken token = sessionHolder.getToken();

            // (1) remove all prior misses for the helper in the given event
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put(MissedAssignment.ATTR_EVENT, RepositoryProvider.getRepository(EventRepository.class).findById(eventId));
            parameters.put(MissedAssignment.ATTR_HELPER, RepositoryProvider.getRepository(HelperRepository.class).findById(helperId));
            Datasources.getDatasource(MissedAssignment.class).executeQuery(token, "DELETE FROM " + MissedAssignment.class.getSimpleName() + " ma WHERE ma.helper = :helper AND ma.event = :event",
                    parameters);

            // (2) persist new miss
            new MissedAssignmentBuilder().withPosition(RepositoryProvider.getRepository(PositionRepository.class).findById(positionId))
                    .withHelper(RepositoryProvider.getRepository(HelperRepository.class).findById(helperId))
                    .withEvent(RepositoryProvider.getRepository(EventRepository.class).findById(eventId))
                    .withTimeStamp()
                    .build()
                    .saveOrUpdate(token);

            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
            ;
            throw new ResourcePlanningException("could not create missed assignment exclusive : " + e.getMessage());
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }

    public List<MissedAssignment> findByPositionAndEvent(Long positionId, Long eventId)
    {
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MissedAssignment.ATTR_POSITION, position);
        parameters.put(MissedAssignment.ATTR_EVENT, event);
        return dataSource().find(null, "FROM " + MissedAssignment.class.getSimpleName() + " ma WHERE ma.position = :position AND ma.event = :event", parameters);
    }
}