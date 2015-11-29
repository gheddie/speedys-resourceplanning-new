package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;

import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HistoryType;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperHistoryRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class HelperInteraction
{
    private static final Logger logger = Logger.getLogger(HelperInteraction.class);
    
    /**
     * called from 'HelperCallbackReceiver.jsp'
     * 
     * @param callback
     * @param businessKey
     * @return
     */
    public static String processReminderCallback(Long eventId, Long helperId, HelperCallback callback)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        switch (callback)
        {
            case ASSIGNMENT_AS_BEFORE:
                logger.info("the helper wants to be assigned as before...");
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
                // write history
                RepositoryProvider.getRepository(HelperHistoryRepository.class).createEntry(helperId, eventId, HistoryType.CALLBACK_ASSIGNMENT_AS_BEFORE);
                break;
            case CHANGE_POS:
                logger.info("the helper wants to change positions...");
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.CHANGE_POS);
                break;
            case PAUSE_ME:
                logger.info("the helper wants to be paused...");
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
                break;
            case ASSIGN_ME_MANUALLY:
                logger.info("the helper wants to manually assigned...");
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGN_ME_MANUALLY);
                break;
        }
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);                        
            return HtmlRenderer.renderCallbackSuccess(helperId, callback);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }

    /**
     * called from 'ChosenPositionReceiver.jsp'
     * 
     * @param eventId
     * @param helperId
     * @param chosenPositionId
     * @param positionAvailable
     * @param request
     * 
     * @throws MismatchingMessageCorrelationException
     */
    public static String processPositionChosenCallback(Long eventId, Long helperId, Long chosenPositionId)
            throws MismatchingMessageCorrelationException
    {
        boolean positionAvailable = PositionService.isPositionAvailable(eventId, chosenPositionId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
            if (positionAvailable)
            {
                // inform the user about position assignment success
                return HtmlRenderer.renderChosenPositionAvailableCallback(helperId, chosenPositionId);
            }
            else
            {
                // inform user about the generation of additional mail ('PROPOSE_POSITIONS')
                // as the chosen position is already assigned to another helper
                return HtmlRenderer.renderChosenPositionUnavailableCallback(helperId, chosenPositionId);
            }
        }
        catch (Exception e)
        {
            // TODO really catch exception here (not MismatchingMessageCorrelationException ?!?)
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }

    public static String processAssignmentCancellation(Long eventId, Long helperId)
    {
        // TODO use correct message here !!
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, businessKey);
            return HtmlRenderer.renderCancellationCallback(helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }
    
    public static String processDeactivationRecovery(Long eventId, Long helperId)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_DEACT_RESP, businessKey);
            return HtmlRenderer.renderDeactivationRecoveryCallback(helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }

    // ---

    public static String getBaseLink()
    {
        return AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.HOST) +
                "/resourceplanning-bpm-" + AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.VERSION);
    }
}