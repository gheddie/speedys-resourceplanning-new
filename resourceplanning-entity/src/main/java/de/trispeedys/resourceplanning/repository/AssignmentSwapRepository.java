package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.AssignmentSwapDatasource;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.SwapState;

public class AssignmentSwapRepository extends AbstractDatabaseRepository<AssignmentSwap> implements DatabaseRepository<AssignmentSwapRepository>
{
    protected DefaultDatasource<AssignmentSwap> createDataSource()
    {
        return new AssignmentSwapDatasource();
    }

    public List<AssignmentSwap> findByEventAndPositionsAndResult(GuidedEvent event, Position sourcePosition, Position targetPosition, SwapState swapResult)
    {
        return dataSource().find(null, AssignmentSwap.ATTR_EVENT, event, AssignmentSwap.ATTR_SOURCE_POSITION, sourcePosition, AssignmentSwap.ATTR_TARGET_POSITION, targetPosition,
                AssignmentSwap.ATTR_SWAP_STATE, swapResult);
    }
    
    public List<AssignmentSwap> findByEvent(GuidedEvent event)
    {
        return dataSource().find(null, AssignmentSwap.ATTR_EVENT, event);        
    }

    public List<AssignmentSwap> findRequestedByEvent(GuidedEvent event)
    {
        return dataSource().find(null, AssignmentSwap.ATTR_EVENT, event, AssignmentSwap.ATTR_SWAP_STATE, SwapState.REQUESTED);        
    }
}