package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class PerformSwapDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        
        if (isSimpleSwap(execution))
        {
            repository.transferHelperAssignment(getSourceAssignment(execution), getTargetPosition(execution));
        }
        else
        {            
            repository.switchHelperAssignments(getSourceAssignment(execution), getTargetAssignment(execution));            
        }
    }
}