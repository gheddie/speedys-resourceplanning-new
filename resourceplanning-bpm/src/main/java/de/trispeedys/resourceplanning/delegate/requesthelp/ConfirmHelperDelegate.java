package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ConfirmHelperDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // at this point, the variable indicating the chosen position must be set, so...
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) == null)
        {
            throw new ResourcePlanningException("can not book helper to position for position id not set!!");
        }
        Long positionId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION);
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).assignHelper(getHelper(execution), getEvent(execution), position, null);
    }
}