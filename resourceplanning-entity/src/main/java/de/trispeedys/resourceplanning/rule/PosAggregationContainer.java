package de.trispeedys.resourceplanning.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Position;

public class PosAggregationContainer
{
    private static final Integer UNPRIORIZED = new Integer(-1);

    // contains all given positions grouped by priority (as key), unpriorized positions get key '-1'
    private HashMap<Integer, List<Position>> positionsByPriority = new HashMap<Integer, List<Position>>();

    private List<Integer> prioKeys = null;

    public PosAggregationContainer(List<Position> positions)
    {
        super();
        Integer prioKey = Integer.MIN_VALUE;
        for (Position pos : positions)
        {
            prioKey = pos.getAssignmentPriority() != null
                    ? pos.getAssignmentPriority()
                    : UNPRIORIZED;
            if (positionsByPriority.get(prioKey) == null)
            {
                positionsByPriority.put(prioKey, new ArrayList<Position>());
            }
            positionsByPriority.get(prioKey).add(pos);
        }
        ArrayList<Integer> tmpKeys = new ArrayList<Integer>(positionsByPriority.keySet());
        Collections.sort(tmpKeys);
        prioKeys = tmpKeys;
    }

    public List<Position> collect()
    {
        // no arms, no cookies...
        if (prioKeys.size() == 0)
        {
            return new ArrayList<Position>();
        }
        // return the positions with the highest prio only...
        Integer highestPriorityKey = prioKeys.get(prioKeys.size() - 1);
        List<Position> result = positionsByPriority.get(highestPriorityKey);
        if ((!(highestPriorityKey.equals(UNPRIORIZED))) && (positionsByPriority.get(UNPRIORIZED) != null))
        {
            // the highest prio is not 'UNPRIORIZED', so we add it, too...
            result.addAll(positionsByPriority.get(UNPRIORIZED));
        }
        return result;
    }
}