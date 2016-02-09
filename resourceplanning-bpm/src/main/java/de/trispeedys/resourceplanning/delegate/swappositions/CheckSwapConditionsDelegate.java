package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractSwapDelegate;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.misc.SwapState;
import de.trispeedys.resourceplanning.entity.misc.SwapType;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.exception.ResourcePlanningSwapException;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.AssignmentSwapRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

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

        // create a swap object in database and put its if into this execution
        SwapType swapType = simpleSwap
                ? SwapType.SIMPLE
                : SwapType.COMPLEX;
        AssignmentSwap swap = EntityFactory.buildAssignmentSwap(getEvent(execution), getSourcePosition(execution), getTargetPosition(execution), swapType, SwapState.REQUESTED);
        
        // check for collisions
        for (AssignmentSwap requestedSwap : RepositoryProvider.getRepository(AssignmentSwapRepository.class).findRequestedByEvent(getEvent(execution)))
        {
            if (swap.collidesWith(requestedSwap))
            {
                throw new ResourcePlanningSwapException("swap collides with another swap of state 'REQUESTED'!");
            }
        }
        
        swap.saveOrUpdate();
        execution.setVariable(BpmVariables.Swap.VAR_SWAP_ENTITY_ID, swap.getId());

        // simple or complex swap?
        execution.setVariable(BpmVariables.Swap.VAR_IS_TO_NULL_SWAP, simpleSwap);
    }
}