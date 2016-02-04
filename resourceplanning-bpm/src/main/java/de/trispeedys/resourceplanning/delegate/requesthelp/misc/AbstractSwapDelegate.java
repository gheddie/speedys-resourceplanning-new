package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;


public abstract class AbstractSwapDelegate extends SpeedyProcessDelegate
{
    protected Position getSourcePosition(DelegateExecution execution)
    {
        Long sourcePositionId = (Long) execution.getVariable(BpmVariables.Swap.VAR_POS_ID_SOURCE);
        Position source = RepositoryProvider.getRepository(PositionRepository.class).findById(sourcePositionId);
        if (source == null)
        {
            throw new ResourcePlanningException("source position with id '"+sourcePositionId+"' could not be found!!");
        }
        return source;
    }

    protected Position getTargetPosition(DelegateExecution execution)
    {
        Long targetPositionId = (Long) execution.getVariable(BpmVariables.Swap.VAR_POS_ID_TARGET);
        Position target = RepositoryProvider.getRepository(PositionRepository.class).findById(targetPositionId);
        if (target == null)
        {
            throw new ResourcePlanningException("target position with id '"+targetPositionId+"' could not be found!!");
        }
        return target;
    }   

    protected HelperAssignment getSourceAssignment(DelegateExecution execution)
    {
        return RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEventAndPosition(getEvent(execution), getSourcePosition(execution));        
    }

    protected HelperAssignment getTargetAssignment(DelegateExecution execution)
    {
        return RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEventAndPosition(getEvent(execution), getTargetPosition(execution));        
    }
    
    protected boolean isSimpleSwap(DelegateExecution execution)
    {
        return (boolean) (execution.getVariable(BpmVariables.Swap.VAR_IS_TO_NULL_SWAP));
    }
}