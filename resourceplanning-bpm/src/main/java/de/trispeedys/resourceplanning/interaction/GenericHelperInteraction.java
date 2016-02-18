package de.trispeedys.resourceplanning.interaction;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;

import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.exception.ResourcePlanningNoSuchEntityException;
import de.trispeedys.resourceplanning.util.htmlgenerator.instance.UnknownEntityHtmlGenerator;
import de.trispeedys.resourceplanning.util.parser.ParserUtil;

public class GenericHelperInteraction
{
    private static final Logger logger = Logger.getLogger(GenericHelperInteraction.class);
    
    /**
     * processes every kind of first step request.
     * @param processEngine 
     */
    public static synchronized String processRequest(HttpServletRequest request, ProcessEngine processEngine)
    {
        RequestType requestType = RequestType.valueOf(request.getParameter("requestType"));
        switch (requestType)
        {
            case REMINDER_CALLBACK:
                Long eventId = ParserUtil.parseLong(request.getParameter("eventId"));
                Long helperId = ParserUtil.parseLong(request.getParameter("helperId"));
                Long priorPositionId = ParserUtil.parseLong(request.getParameter("priorPositionId"));
                HelperCallback callback = HelperCallback.valueOf(request.getParameter("callbackResult"));
                return HelperInteraction.processReminderCallback(eventId, helperId, priorPositionId, callback, processEngine);
            default:
                return null;
        }
    }
    
    /**
     * processes every kind of confirmation request.
     */
    public static synchronized String processConfirmation(HttpServletRequest request, RequestType requestType, ProcessEngine processEngine)
    {
        return null;
    }
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Request help process --------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    
    /*
    private static String processReminderCallback(Long eventId, Long helperId, Long priorPositionId, HelperCallback callback,
            ProcessEngine testEngine)
    {
        try
        {
            HelperInteraction.checkEntitiesForExistence(eventId, helperId, priorPositionId);
            
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
    */
    
    //---------------------------------------------------------------------------------------------------------------------------------------
    //--- Swap process ----------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------
    
    // ...
}