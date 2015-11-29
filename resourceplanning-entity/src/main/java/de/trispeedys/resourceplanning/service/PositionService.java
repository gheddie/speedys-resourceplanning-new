package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class PositionService
{
    public static boolean isPositionAvailable(Long eventId, Long positionId)
    {
        Position position = (Position) Datasources.getDatasource(Position.class).findById(positionId);
        if (position == null)
        {
            throw new ResourcePlanningException("position with id '" + positionId + "' could not be found!!");
        }
        Event event = (Event) Datasources.getDatasource(Event.class).findById(eventId);
        return isPositionAvailable(event, position);
    }

    /**
     * checks if the given position is present and already assigned in the given event
     * 
     * @param eventId
     * @param position
     * @return
     */
    public static boolean isPositionAvailable(Event event, Position position)
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
        List<HelperAssignment> helperAssignments = Datasources.getDatasource(HelperAssignment.class).find(queryString, variables);
        // no helper assignments -> position available
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * checks if the given {@link Position} is assigned to the {@link Event} by a {@link EventPosition} entry.
     * 
     * @param position
     * @param event
     * @return
     */
    public static boolean isPositionPresentInEvent(Position position, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("position", position);
        parameters.put("event", event);
        List<?> result =
                Datasources.getDatasource(EventPosition.class)
                        .find("FROM " +
                                EventPosition.class.getSimpleName() + " ep WHERE ep.position = :position AND ep.event = :event",
                                parameters);
        return ((result != null) && (result.size() > 0));
    }
}