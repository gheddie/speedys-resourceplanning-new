package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;

import de.trispeedys.resourceplanning.BpmHelper;
import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.swap.TriggerComplexSwapMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class HelperConfirmation
{
    private static final int MAX_MESSAGE_LENGTH = 250;

    private static final String ERROR_MESSAGE_TOO_LONG = "ERROR_MESSAGE_TOO_LONG";
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Request help process --------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------

    public static synchronized String processCancelForeverConfirmation(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.QUIT_FOREVER);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        // find out if the chosen position is available and feed that information to the process...
        boolean positionAvailable = RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(eventId, chosenPositionId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);

        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_RECOVERY, businessKey, variables);
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
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

        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGN_ME_MANUALLY);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_MANUAL_ASSIGNMENT_WISH, helperMessage);
        try
        {
            // correlate message 'MSG_HELP_CALLBACK' with peculiarity 'ASSIGN_ME_MANUALLY'...
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(
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
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.CHANGE_POS);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
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
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Swap process ----------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------

    public static synchronized String processComplexSwapResponse(Long eventId, Long positionIdSource, Long positionIdTarget, boolean swapOk, String trigger, ProcessEngine testEngine)
    {
        String businessKey = BusinessKeys.generateSwapBusinessKey(eventId, positionIdSource, positionIdTarget);
        Map<String, Object> variables = new HashMap<>();
        
        switch (trigger)
        {
            case TriggerComplexSwapMailTemplate.TRIGGER_SOURCE:
                // ------------------------------------------
                variables.put(BpmVariables.Swap.VAR_NOT_TO_NULL_SWAP_SOURCE_OK, swapOk);
                BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_SWAP_ANSW_SOURCE, businessKey, variables);
                return "VAR_NOT_TO_NULL_SWAP_SOURCE_OK";
                // ------------------------------------------
            case TriggerComplexSwapMailTemplate.TRIGGER_TARGET:
                // ------------------------------------------
                variables.put(BpmVariables.Swap.VAR_NOT_TO_NULL_SWAP_TARGET_OK, swapOk);
                BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_SWAP_ANSW_TARGET, businessKey, variables);
                return "VAR_NOT_TO_NULL_SWAP_TARGET_OK";
                // ------------------------------------------
        }
        // will not occur
        return null;
    }
    
    public static synchronized String processSimpleSwapResponse(Long eventId, Long positionIdSource, Long positionIdTarget, boolean swapOk, String trigger, ProcessEngine testEngine)
    {
        String businessKey = BusinessKeys.generateSwapBusinessKey(eventId, positionIdSource, positionIdTarget);
        Map<String, Object> variables = new HashMap<>();
        variables.put(BpmVariables.Swap.VAR_TO_NULL_SWAP_OK, true);
        BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_SIMPLE_SWAP_ANSW, businessKey, variables);
        return "processSimpleSwapResponse";
    }
}