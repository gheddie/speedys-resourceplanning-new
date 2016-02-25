package de.trispeedys.resourceplanning.delegate.swappositions;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
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
import de.trispeedys.resourceplanning.repository.HelperRepository;

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
        
        Long helperIdSource = (Long) execution.getVariable(BpmVariables.Swap.VAR_HELPER_ID_SOURCE);
        Long helperIdTarget = (Long) execution.getVariable(BpmVariables.Swap.VAR_HELPER_ID_TARGET);

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
            // there must be a source helper
            if (helperIdSource == null)
            {
                throw new ResourcePlanningException("source helper id must be set in order to start a simple swap process!");
            }
        }
        else if ((sourceAssignment != null) && (targetAssignment != null))
        {
            simpleSwap = false;
            // there must be a source and a target helper
            if ((helperIdSource == null) || (helperIdTarget == null))
            {
                throw new ResourcePlanningException("source and target helper id must be set in order to start a complex swap process!");
            }
        }

        // create a swap object in database and put its if into this execution
        SwapType swapType = simpleSwap
                ? SwapType.SIMPLE
                : SwapType.COMPLEX;
        AssignmentSwap swap = null;
        HelperRepository helperRepository = RepositoryProvider.getRepository(HelperRepository.class);
        switch (swapType)
        {
            case SIMPLE:
                swap =
                        EntityFactory.buildAssignmentSwap(getEvent(execution), getSourcePosition(execution), getTargetPosition(execution),
                                swapType, SwapState.REQUESTED, helperRepository.findById(helperIdSource), null);
                break;
            case COMPLEX:
                swap =
                        EntityFactory.buildAssignmentSwap(getEvent(execution), getSourcePosition(execution), getTargetPosition(execution),
                                swapType, SwapState.REQUESTED, helperRepository.findById(helperIdSource),
                                helperRepository.findById(helperIdTarget));
                break;
        }

        swap.saveOrUpdate();
        execution.setVariable(BpmVariables.Swap.VAR_SWAP_ENTITY_ID, swap.getId());

        // simple or complex swap?
        execution.setVariable(BpmVariables.Swap.VAR_IS_TO_NULL_SWAP, simpleSwap);
    }
}