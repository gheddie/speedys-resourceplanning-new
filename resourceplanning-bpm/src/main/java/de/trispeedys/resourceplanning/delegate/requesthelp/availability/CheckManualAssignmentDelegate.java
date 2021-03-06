package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class CheckManualAssignmentDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) == null)
        {
            // TODO translate
            throw new ResourcePlanningException("chosen position id must not be null at this point!!");
        }
        GuidedEvent event = getEvent(execution);
        Long positionId = (Long) execution.getVariableLocal(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION);
        if (RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(event.getId(), positionId))
        {
            execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, true);
        }
        else
        {
            throw new ResourcePlanningException("chosen position is not available!!");
        }
    }
}