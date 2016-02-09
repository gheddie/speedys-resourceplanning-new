package de.trispeedys.resourceplanning.interaction;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;

import de.trispeedys.resourceplanning.BpmHelper;
import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.exception.ResourcePlanningNoSuchEntityException;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.messaging.template.helprequest.AlertPlanningExceptionMailTemplate;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.htmlgenerator.instance.UnknownEntityHtmlGenerator;

public class HelperInteraction
{
    private static final Logger logger = Logger.getLogger(HelperInteraction.class);
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Request help process --------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------

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
        try
        {
            checkEntitiesForExistence(eventId, helperId, priorPositionId);
            
            logger.info("the helper has chosen : " + callback);
            switch (callback)
            {
                case ASSIGN_ME_MANUALLY:
                    //------------------------------------------
                    // manual assignment must be treated seperately as the helper
                    // gets a chance to enter a comment --> no direct message correlation
                    return JspRenderer.renderManualAssignmentForm(eventId, helperId);
                    //------------------------------------------
                case QUIT_FOREVER:
                    //------------------------------------------
                    // before completing this, helper must confirm...
                    return JspRenderer.renderCancelForeverForm(eventId, helperId);
                    //------------------------------------------
                case PAUSE_ME:
                    //------------------------------------------
                    // before completing this, helper must confirm...
                    return JspRenderer.renderPauseMeForm(eventId, helperId);
                    //------------------------------------------
                case ASSIGNMENT_AS_BEFORE:
                    //------------------------------------------
                    // before completing this, helper must confirm...
                    return JspRenderer.renderAssignmentAsBeforeForm(eventId, helperId, priorPositionId);
                    //------------------------------------------
                case CHANGE_POS:
                    //------------------------------------------
                    // before completing this, helper must confirm...
                    return JspRenderer.renderChangePositionForm(eventId, helperId, priorPositionId);
                    //------------------------------------------
                default:
                    // fall through --> will not occur...
                    return null;
            }            
        }
        catch (ResourcePlanningNoSuchEntityException e)
        {
            return new UnknownEntityHtmlGenerator(e.getMessage()).render();
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
        checkEntitiesForExistence(eventId, helperId, chosenPositionId);
        return JspRenderer.renderPositionChosenForm(eventId, helperId, chosenPositionId);
    }

    public static synchronized String processAssignmentCancellation(Long eventId, Long helperId,
            Long positionId, ProcessEngine testEngine)
    {
        checkEntitiesForExistence(eventId, helperId, positionId);
        return JspRenderer.renderAssignmentCancellationForm(eventId, helperId, positionId);
    }

    public static synchronized String processDeactivationRecovery(Long eventId, Long helperId, ProcessEngine testEngine)
    {
        checkEntitiesForExistence(eventId, helperId, null);
        
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmHelper.getProcessEngine(testEngine).getRuntimeService().correlateMessage(
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
        checkEntitiesForExistence(eventId, helperId, chosenPositionId);
        return JspRenderer.renderPositionRecoveryOnCancellationForm(eventId, helperId, chosenPositionId);
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
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (helperId != null)
        {
            Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
            AlertPlanningExceptionMailTemplate template =
                    new AlertPlanningExceptionMailTemplate(helper, event, null, message);
            RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                    AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL),
                    template.constructSubject(), template.constructBody(), template.getMessagingType(), true, null);   
        }
        else
        {
            AlertPlanningExceptionMailTemplate template =
                    new AlertPlanningExceptionMailTemplate(null, event, null, message);
            RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                    AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL),
                    template.constructSubject(), template.constructBody(), template.getMessagingType(), true, null);  
        }
    }
    
    private static void checkEntitiesForExistence(Long eventId, Long helperId, Long priorPositionId)
    {
        checkEntityForExistence(eventId, Event.class);
        checkEntityForExistence(helperId, Helper.class);
        checkEntityForExistence(priorPositionId, Position.class);
    }

    private static void checkEntityForExistence(Long primaryKeyValue, Class<? extends AbstractDbObject> entityClass)
    {
        if (primaryKeyValue == null)
        {
            return;
        }
        if (Datasources.getDatasource(entityClass).findById(null, primaryKeyValue) == null)
        {
            // thats a mistake --> no object found for a given ID!!!
            throw new ResourcePlanningNoSuchEntityException(entityClass, primaryKeyValue);
        }
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Swap process ----------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------    
}