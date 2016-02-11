package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class PerformSwapDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        
        HelperAssignment sourceAssignment = getSourceAssignment(execution);
        HelperAssignment targetAssignment = getTargetAssignment(execution);
        
        if (isSimpleSwap(execution))
        {
            repository.transferHelperAssignment(sourceAssignment, getTargetPosition(execution));
        }
        else
        {                        
            repository.switchHelperAssignments(sourceAssignment, targetAssignment);            
        }
        
        // TODO set assignment swap to 'COMPLETED' !!
        AssignmentSwap swap = getSwapEntity(execution);
        swap.setSwapState(SwapState.COMPLETED);
        swap.saveOrUpdate();
    }
}