package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class MissedAssignmentPositionChosenDelegate extends MissedAssignmentDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        RepositoryProvider.getRepository(MissedAssignmentRepository.class).createMissedAssignmentExclusive(
                (Long) execution.getVariable(BpmVariables.Misc.VAR_EVENT_ID),
                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID),
                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));
    }
}