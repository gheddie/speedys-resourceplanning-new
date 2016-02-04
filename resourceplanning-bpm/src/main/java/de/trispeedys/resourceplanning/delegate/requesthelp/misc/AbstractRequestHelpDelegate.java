package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public abstract class AbstractRequestHelpDelegate extends SpeedyProcessDelegate
{
    protected Helper getHelper(DelegateExecution execution)
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        if (helper == null)
        {
            throw new ResourcePlanningException("event with id '"+helperId+"' could not be found!!");
        }
        return helper;
    }
}