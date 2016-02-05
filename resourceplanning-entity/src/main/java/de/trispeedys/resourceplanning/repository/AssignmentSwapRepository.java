package de.trispeedys.resourceplanning.repository;

import java.util.List;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.AssignmentSwapDatasource;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.SwapResult;

public class AssignmentSwapRepository extends AbstractDatabaseRepository<AssignmentSwap> implements DatabaseRepository<AssignmentSwapRepository>
{
    protected DefaultDatasource<AssignmentSwap> createDataSource()
    {
        return new AssignmentSwapDatasource();
    }

    public List<AssignmentSwap> findByEventAndPositionsAndResult(Event event, Position sourcePosition, Position targetPosition, SwapResult swapResult)
    {
        return dataSource().find(null, AssignmentSwap.ATTR_EVENT, event, AssignmentSwap.ATTR_SOURCE_POSITION, sourcePosition, AssignmentSwap.ATTR_TARGET_POSITION, targetPosition,
                AssignmentSwap.ATTR_SWAP_RESULT, swapResult);
    }
}