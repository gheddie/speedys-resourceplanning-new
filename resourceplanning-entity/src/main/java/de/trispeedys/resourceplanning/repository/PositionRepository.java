package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.rule.ChoosablePositionGenerator;

public class PositionRepository extends AbstractDatabaseRepository<Position> implements
        DatabaseRepository<PositionRepository>
{
    public Position findPositionByPositionNumber(int positionNumber)
    {
        List<Position> result = dataSource().find(null, Position.ATTR_POS_NUMBER, positionNumber);
        return safeValue(result);
    }

    public List<Position> findUnassignedPositionsByGenerator(Helper helper, GuidedEvent event)
    {
        return new ChoosablePositionGenerator().generate(helper, event);
    }

    /**
     * finds all unassigned positions in an event. for every position the aptness concerning the given helpers age is
     * checked.
     * 
     * @param event
     * @param helper
     * @param onlyChooseable
     * @return
     */
    public List<Position> findUnassignedPositionsInEvent(GuidedEvent event, Helper helper, boolean onlyChooseable)
    {
        // get all unassigned positions
        List<Position> positions = findUnassignedPositionsInEvent(event, onlyChooseable);
        if (helper == null)
        {
            // positions will NOT be checked against helpers age as no helper is set -> just return positions
            return positions;
        }
        else
        {
            List<Position> result = new ArrayList<Position>();
            // positions will be checked against helpers age
            for (Position pos : positions)
            {
                if (helper.isAssignableTo(pos, event.getEventDate()))
                {
                    result.add(pos);
                }
            }
            return result;
        }
    }

    public List<Position> findUnassignedPositionsInEvent(GuidedEvent event, boolean onlyChooseable)
    {
        // all event positions not referenced by an assigment which is 'PLANNED' or 'CONFIRMED'
        String qryString =
                "FROM " +
                        EventPosition.class.getSimpleName() + " ep WHERE ep." + EventPosition.ATTR_EVENT +
                        " = :event AND ep.position.id NOT IN (SELECT ec.position.id FROM " +
                        HelperAssignment.class.getSimpleName() +
                        " ec WHERE ec.event = :event AND" +
                        // helper assignments must from both states 'PLANNED' and 'CONFIRMED' must be
                        // regarded (and grouped) as exclusions
                        " (ec.helperAssignmentState = '" + HelperAssignmentState.PLANNED +
                        "' OR ec.helperAssignmentState = '" + HelperAssignmentState.CONFIRMED + "'))";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        List<EventPosition> eventPositions =
                Datasources.getDatasource(EventPosition.class).find(null, qryString, parameters);
        List<Position> result = new ArrayList<Position>();
        Position position = null;
        for (EventPosition ep : eventPositions)
        {
            position = ep.getPosition();
            if (onlyChooseable)
            {
                // add position only if it is choosable
                if (position.isChoosable())
                {
                    result.add(position);
                }
            }
            else
            {
                // add unconditionally
                result.add(position);
            }
        }
        return result;
    }

    public List<Position> findPositionsInEvent(GuidedEvent event)
    {
        List<EventPosition> list =
                Datasources.getDatasource(EventPosition.class).find(
                        null,
                        "FROM " +
                                EventPosition.class.getSimpleName() +
                                " ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
        List<Position> result = new ArrayList<Position>();
        Object[] tuple = null;
        for (Object obj : list)
        {
            tuple = (Object[]) obj;
            result.add((Position) tuple[1]);
        }
        return result;
    }

    public boolean isPositionAvailable(Long eventId, Long positionId)
    {
        Position position = (Position) Datasources.getDatasource(Position.class).findById(null, positionId);
        if (position == null)
        {
            throw new ResourcePlanningException("position with id '" + positionId + "' could not be found!!");
        }
        GuidedEvent event = (GuidedEvent) Datasources.getDatasource(GuidedEvent.class).findById(null, eventId);
        return isPositionAvailable(event, position);
    }

    /**
     * checks if the given position is present and already assigned in the given event
     * 
     * @param eventId
     * @param position
     * @return
     */
    public boolean isPositionAvailable(GuidedEvent event, Position position)
    {
        if (!(isPositionPresentInEvent(position, event)))
        {
            return false;
        }
        // pos is not available if we find an assignment which is not cancelled
        String queryString =
                "FROM " +
                        HelperAssignment.class.getSimpleName() +
                        " ec WHERE ec.position = :position AND ec.event = :event AND ec.helperAssignmentState <> '" +
                        HelperAssignmentState.CANCELLED + "'";
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put(HelperAssignment.ATTR_EVENT, event);
        variables.put(HelperAssignment.ATTR_POSITION, position);
        List<HelperAssignment> helperAssignments =
                Datasources.getDatasource(HelperAssignment.class).find(null, queryString, variables);
        // no helper assignments -> position available
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * checks if the given {@link Position} is assigned to the {@link GuidedEvent} by a {@link EventPosition} entry.
     * 
     * @param position
     * @param event
     * @return
     */
    public boolean isPositionPresentInEvent(Position position, GuidedEvent event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("position", position);
        parameters.put("event", event);
        List<?> result =
                Datasources.getDatasource(EventPosition.class).find(
                        null,
                        "FROM " +
                                EventPosition.class.getSimpleName() +
                                " ep WHERE ep.position = :position AND ep.event = :event", parameters);
        return ((result != null) && (result.size() > 0));
    }

    protected DefaultDatasource<Position> createDataSource()
    {
        return new PositionDatasource();
    }

    public List<Position> findEventPositions(GuidedEvent event, boolean includedInEvent)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        String condition = null;
        if (includedInEvent)
        {
            condition = " WHERE p.id IN";
        }
        else
        {
            condition = " WHERE p.id NOT IN";
        }
        String qryString =
                "FROM " +
                        Position.class.getSimpleName() + " p" + condition + " (SELECT ep.position.id FROM " +
                        EventPosition.class.getSimpleName() + " ep WHERE ep.event = :event)";
        return dataSource().find(null, qryString, parameters);
    }

    public Position createPosition(String description, int positionNumber, Domain domain, int minimalAge,
            boolean choosable)
    {
        return EntityFactory.buildPosition(description, minimalAge, domain, positionNumber, choosable).saveOrUpdate();
    }
}