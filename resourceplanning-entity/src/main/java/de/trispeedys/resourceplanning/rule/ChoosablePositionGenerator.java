package de.trispeedys.resourceplanning.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.repository.AggregationRelationRepository;
import de.trispeedys.resourceplanning.repository.PositionAggregationRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;

/**
 * @author Stefan.Schulz
 */
public class ChoosablePositionGenerator extends RuleObject<Position>
{
    private static final String NO_GROUP = "NO_GROUP";
    
    public List<Position> generate(GuidedEvent event)
    {
        return generate(null, event);
    }

    public List<Position> generate(Helper helper, GuidedEvent event)
    {
        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        List<Position> unassignedPositionsInEvent = positionRepository.findUnassignedPositionsInEvent(event, helper, true);
        if ((unassignedPositionsInEvent == null) || (unassignedPositionsInEvent.size() == 0))
        {
            // no positions available, so...
            return new ArrayList<Position>();
        }
        List<PositionAggregation> groups = RepositoryProvider.getRepository(PositionAggregationRepository.class).findAll(null);
        if ((groups == null) || (groups.size() == 0))
        {
            // we take all positions as one group...
            return new PosAggregationContainer(unassignedPositionsInEvent).collect();
        }
        else
        {
            // cache unassigned positions...
            HashMap<Long, Position> positionHash = new HashMap<Long, Position>();
            for (Position pos : unassignedPositionsInEvent)
            {
                positionHash.put(pos.getId(), pos);
            }
            // all relations between position and aggregation...
            HashMap<String, List<Position>> positionsByGroup = new HashMap<String, List<Position>>();
            Position pos = null;
            for (AggregationRelation relation : RepositoryProvider.getRepository(AggregationRelationRepository.class).findAll(null))
            {
                String key = relation.getPositionAggregation().getName();
                if (positionsByGroup.get(key) == null)
                {
                    positionsByGroup.put(key, new ArrayList<Position>());
                }
                pos = positionHash.get(relation.getPositionId());
                if (pos != null)
                {
                    // pos may be null if it is in a aggregation
                    // but not available at this point of time...
                    positionsByGroup.get(key).add(pos);
                    // remove used position
                    positionHash.remove(relation.getPositionId());   
                }
            }
            // add remaining positions to group 'no group'...
            positionsByGroup.put(NO_GROUP, new ArrayList<Position>(positionHash.values()));
            List<Position> result = new ArrayList<Position>();
            for (String name : positionsByGroup.keySet())
            {
                result.addAll(new PosAggregationContainer(positionsByGroup.get(name)).collect());
            }
            return result;
        }
    }
}