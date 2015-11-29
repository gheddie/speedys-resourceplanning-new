package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;
import de.trispeedys.resourceplanning.rule.ChoosablePositionGenerator;

public class PositionRepository extends AbstractDatabaseRepository<Position> implements DatabaseRepository<PositionRepository>
{
    public Position findPositionByPositionNumber(int positionNumber)
    {
        return (Position) dataSource().findSingle(Position.ATTR_POS_NUMBER, positionNumber);
    }

    public List<Position> findUnassignedPositionsByGenerator(Helper helper, Event event)
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
    public List<Position> findUnassignedPositionsInEvent(Event event, Helper helper, boolean onlyChooseable)
    {
        List<Position> result = new ArrayList<Position>();
        // get all unassigned positions
        for (Position pos : findUnassignedPositionsInEvent(event, onlyChooseable))
        {
            if (helper.isAssignableTo(pos, event.getEventDate()))
            {
                result.add(pos);
            }
        }
        return result;
    }

    public List<Position> findUnassignedPositionsInEvent(Event event, boolean onlyChooseable)
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
                        " (ec.helperAssignmentState = '" + HelperAssignmentState.PLANNED + "' OR ec.helperAssignmentState = '" +
                        HelperAssignmentState.CONFIRMED + "'))";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        List<EventPosition> eventPositions = Datasources.getDatasource(EventPosition.class).find(qryString, parameters);
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

    public List<Position> findPositionsInEvent(Event event)
    {
        List<EventPosition> list =
                Datasources.getDatasource(EventPosition.class).find(
                        "FROM " + EventPosition.class.getSimpleName() + " ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
        List<Position> result = new ArrayList<Position>();
        Object[] tuple = null;
        for (Object obj : list)
        {
            tuple = (Object[]) obj;
            result.add((Position) tuple[1]);
        }
        return result;
    }

    protected DefaultDatasource<Position> createDataSource()
    {
        return new PositionDatasource();
    }
}