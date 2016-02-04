package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class CheckSwapConditionsDelegate extends AbstractSwapDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // event id must be set in process variables
        if (execution.getVariable(BpmVariables.Misc.VAR_EVENT_ID) == null)
        {
            throw new ResourcePlanningException("event id must be set!!");
        }
        
        boolean simpleSwap = false;
        
        Long posIdSource = (Long) execution.getVariable(BpmVariables.Swap.VAR_POS_ID_SOURCE);
        Long posIdTarget = (Long) execution.getVariable(BpmVariables.Swap.VAR_POS_ID_TARGET);
        
        // assignments
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        HelperAssignment sourceAssignment = repository.findByEventAndPositionId(getEvent(execution), posIdSource);
        HelperAssignment targetAssignment = repository.findByEventAndPositionId(getEvent(execution), posIdTarget);
        
        if ((sourceAssignment == null) && (targetAssignment == null))
        {
            throw new ResourcePlanningException("at least one assignment id must be there!!"); 
        }
        else if ((sourceAssignment == null) && (targetAssignment != null))
        {
            throw new ResourcePlanningException("wrong disposition --> if only one assignment set, it must be the source!!");
        }
        else if ((sourceAssignment != null) && (targetAssignment == null))
        {
            simpleSwap = true;
        }        
        else if ((sourceAssignment != null) && (targetAssignment != null))
        {
            simpleSwap = false;
        }        
        
        // write back position ids...
        execution.setVariable(BpmVariables.Swap.VAR_POS_ID_SOURCE, posIdSource);
        execution.setVariable(BpmVariables.Swap.VAR_POS_ID_TARGET, posIdTarget);
        
        // simple or complex swap?        
        execution.setVariable(BpmVariables.Swap.VAR_IS_TO_NULL_SWAP, simpleSwap);
    }
}