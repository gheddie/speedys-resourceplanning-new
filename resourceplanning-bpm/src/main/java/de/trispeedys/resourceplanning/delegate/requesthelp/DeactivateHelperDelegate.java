package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class DeactivateHelperDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        RepositoryProvider.getRepository(HelperRepository.class).deactivateHelper((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
    }
}