package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.StringUtil;

public class CheckHelperConditionDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(null, helperId);
        boolean firstAssignment = RepositoryProvider.getRepository(HelperAssignmentRepository.class).isFirstAssignment(helperId);   
        if ((firstAssignment) || (StringUtil.isBlank(helper.getEmail())))
        {
            execution.setVariable(
                    BpmVariables.RequestHelpHelper.VAR_SUPERVISION_REQUIRED,
                    true);
        }
        else
        {
            execution.setVariable(
                    BpmVariables.RequestHelpHelper.VAR_SUPERVISION_REQUIRED,
                    false);
        }
        //set mails attempts to 0
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, 0);
    }
}