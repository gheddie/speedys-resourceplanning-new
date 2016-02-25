package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class CheckPreconditionsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // (1) check if business key is set
        if (execution.getBusinessKey() == null)
        {
            throw new ResourcePlanningException("can not start request help process without a business key!!");
        }

        // (2) check if helper id is set
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        if (helperId == null)
        {
            throw new ResourcePlanningException("can not start request help process without a helper id set!!");
        }

        // (3) check if event id is set
        if (execution.getVariable(BpmVariables.Misc.VAR_EVENT_ID) == null)
        {
            throw new ResourcePlanningException("can not start request help process without a event id set!!");
        }

        // set helper code in process instance
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CODE, SpeedyRoutines.createHelperCode(RepositoryProvider.getRepository(HelperRepository.class).findById(helperId)));
    }
}