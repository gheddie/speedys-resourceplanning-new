package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;

public class CheckHelperConditionDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        /*
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CODE) == null)
        {
            throw new ResourcePlanningException("helper code must not be NULL!!");
        }
        */
        
        AppConfiguration.getInstance();
        
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        boolean firstAssignment = AssignmentService.isFirstAssignment(helperId);   
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