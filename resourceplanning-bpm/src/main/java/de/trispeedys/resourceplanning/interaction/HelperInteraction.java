package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.AlertPlanningExceptionMailTemplate;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

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
    public static synchronized String processReminderCallback(Long eventId, Long helperId, Long priorPositionId, HelperCallback callback,
            ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        Map<String, Object> variables = new HashMap<String, Object>();
        
        logger.info("the helper has chosen : " + callback);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, callback);
        
        if (callback.equals(HelperCallback.ASSIGN_ME_MANUALLY))
        {
            // manual assignment must be treated seperately as the helper
            // gets a chance to enter a comment --> no direct message correlation
            return JspRenderer.renderManualAssignmentForm(eventId, helperId);
        }
        else if (callback.equals(HelperCallback.QUIT_FOREVER))
        {
            // before completing this, helper must confirm...
            return JspRenderer.renderCancelForeverForm(eventId, helperId);
        }
        else if (callback.equals(HelperCallback.PAUSE_ME))
        {
            // before completing this, helper must confirm...
            return JspRenderer.renderPauseMeForm(eventId, helperId);
        }
        else if (callback.equals(HelperCallback.ASSIGNMENT_AS_BEFORE))
        {
            // before completing this, helper must confirm...
            return JspRenderer.renderAssignmentAsBeforeForm(eventId, helperId, priorPositionId);
        }  
        else
        {
            //unconfirmed options (no following form)...
            try
            {
                getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                        BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
                return JspRenderer.renderCallbackSuccess(eventId, helperId, callback);
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
                alertPlanningException(helperId, eventId, e.getMessage());
                return JspRenderer.renderPlanningException(helperId, e.getMessage());
            }
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
    public static synchronized String processPositionChosenCallback(Long eventId, Long helperId, Long chosenPositionId,
            ProcessEngine testEngine) throws MismatchingMessageCorrelationException
    {
        // find out if the chosen position is available and feed that information to the process...
        boolean positionAvailable =
                RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(eventId,
                        chosenPositionId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            getProcessEngine(testEngine).getRuntimeService().correlateMessage(
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
            alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processAssignmentCancellation(Long eventId, Long helperId,
            ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                    BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, businessKey);
            return JspRenderer.renderCancellationCallback(helperId);
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
            alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processDeactivationRecovery(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                    BpmMessages.RequestHelpHelper.MSG_DEACT_RESP, businessKey);
            return JspRenderer.renderDeactivationRecoveryCallback(helperId);
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
            alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static synchronized String processPositionRecoveryOnCancellation(Long eventId, Long helperId,
            Long chosenPositionId, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);

        // find out if the chosen position is available and feed that information to the process...
        boolean positionAvailable =
                RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(eventId,
                        chosenPositionId);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);

        try
        {
            getProcessEngine(testEngine).getRuntimeService().correlateMessage(
                    BpmMessages.RequestHelpHelper.MSG_ASSIG_RECOVERY, businessKey, variables);
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
            alertPlanningException(helperId, eventId, e.getMessage());
            return JspRenderer.renderPlanningException(helperId, e.getMessage());
        }
    }

    public static ProcessEngine getProcessEngine(ProcessEngine testEngine)
    {
        // return test engine if set, default engine from bpm platform otherwise
        return testEngine != null
                ? testEngine
                : BpmPlatform.getDefaultProcessEngine();
    }

    /**
     * generates a mail to the admin informing him about a problem caused by by a {@link ResourcePlanningException}
     * which occured while processing a helper interaction.
     * 
     * @param helperId
     * @param eventId
     * @param message
     */
    public static void alertPlanningException(Long helperId, Long eventId, String message)
    {
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        AlertPlanningExceptionMailTemplate template =
                new AlertPlanningExceptionMailTemplate(helper, event, null, message);
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL),
                template.constructSubject(), template.constructBody(), template.getMessagingType(), true, null);
    }
}