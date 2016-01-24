package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class HelperConfirmation
{
    private static final int MAX_MESSAGE_LENGTH = 250;

    private static final String ERROR_MESSAGE_TOO_LONG = "ERROR_MESSAGE_TOO_LONG";

    public static synchronized String processCancelForeverConfirmation(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.QUIT_FOREVER);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return JspRenderer.renderCancelForeverConfirmation(eventId, helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }
    
    public static synchronized String processPositionRecoveryConfirmation(Long eventId, Long helperId, Long chosenPositionId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        // find out if the chosen position is available and feed that information to the process...
        boolean positionAvailable = RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(eventId, chosenPositionId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);

        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_RECOVERY, businessKey, variables);
            return JspRenderer.renderPositionRecoveryOnCancellation(eventId, helperId, chosenPositionId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processPauseMeConfirmation(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return JspRenderer.renderPauseMeConfirmation(eventId, helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processManualAssignmentConfirmation(Long eventId, Long helperId, String helperMessage, ProcessEngine testEngine)
    {
        if ((!(StringUtil.isBlank(helperMessage))) && (helperMessage.length() > MAX_MESSAGE_LENGTH))
        {
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(HelperConfirmation.class, ERROR_MESSAGE_TOO_LONG, helperMessage.length(), MAX_MESSAGE_LENGTH));
        }

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGN_ME_MANUALLY);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_MANUAL_ASSIGNMENT_WISH, helperMessage);
        try
        {
            // correlate message 'MSG_HELP_CALLBACK' with peculiarity 'ASSIGN_ME_MANUALLY'...
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return JspRenderer.renderManualAssignmentConfirmation(eventId, helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processAssignmentAsBeforeConfirmation(Long eventId, Long helperId, Long priorPositionId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return JspRenderer.renderAssignmentAsBeforeConfirmation(eventId, helperId, priorPositionId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }
    
    public static synchronized String processAssignmentCancellationConfirm(Long eventId, Long helperId, Long priorPositionId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                    BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, businessKey);
            return JspRenderer.renderCancellationConfirm(helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }
    
    public static synchronized String processPositionChosenConfirmation(Long eventId, Long helperId, Long chosenPositionId, ProcessEngine testEngine)
    {
        boolean positionAvailable =
                RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(eventId,
                        chosenPositionId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                    BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
            if (positionAvailable)
            {
                // inform the user about position assignment success
                return JspRenderer.renderChosenPositionAvailableCallback(helperId, chosenPositionId);
            }
            else
            {
                // inform user about the generation of additional mail ('PROPOSE_POSITIONS')
                // as the chosen position is already assigned to another helper
                return JspRenderer.renderChosenPositionUnavailableCallback(helperId, chosenPositionId);
            }
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processChangePositionConfirmation(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.CHANGE_POS);
        try
        {
            HelperInteraction.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return JspRenderer.rendeChangePositionConfirmation(eventId, helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return JspRenderer.renderCorrelationFault(helperId);
        }
        catch (ProcessEngineException e)
        {
            return JspRenderer.renderGenericEngineFault(helperId, e.getMessage());
        }
        catch (ResourcePlanningException e)
        {
            // this is an exception raised from the business logic...
            HelperInteraction.alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }
}