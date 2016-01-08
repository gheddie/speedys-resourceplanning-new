package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.PositionRecoveryOnCancellationMailTemplate;
import de.trispeedys.resourceplanning.messaging.template.PlanningSuccessMailTemplate;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EventManager
{
    private static final Logger logger = Logger.getLogger(EventManager.class);

    private static final String PLANNING_IN_PROGRESS = "PLANNING_IN_PROGRESS";

    public static final boolean RECOVER_CANCELLED_POSITIONS = true;

    public static void triggerHelperProcesses(String templateName)
    {
        if (StringUtil.isBlank(templateName))
        {
            throw new ResourcePlanningException("template name must not be blank!!");
        }
        List<Event> events =
                RepositoryProvider.getRepository(EventRepository.class).findEventsByTemplateAndStatus(templateName,
                        EventState.PLANNED);
        if ((events == null) || (events.size() != 1))
        {
            throw new ResourcePlanningException("there must be exactly one planned event of template '" +
                    templateName + "'!!");
        }
        Event event = events.get(0);
        if (!(event.getEventState().equals(EventState.PLANNED)))
        {
            throw new ResourcePlanningException("event must have state '" + EventState.PLANNED + "'!!");
        }
        // start request process for every active helper...
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : activeHelpers)
        {
            startHelperRequestProcess(helper, event);
        }
        // update event state
        RepositoryProvider.getRepository(EventRepository.class).updateEventState(event, EventState.INITIATED);

        informAdminAboutSuccess(event);
    }

    public static void triggerHelperProcesses(Long eventId)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();

        if (configuration.isPlanningInProgress())
        {
            // planning in progress --> exception
            throw new ResourcePlanningException(configuration.getText(EventManager.class, PLANNING_IN_PROGRESS));
        }

        try
        {
            configuration.setPlanningInProgress(true);

            if (eventId == null)
            {
                throw new ResourcePlanningException("event id must not be null!!");
            }
            Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
            if (event == null)
            {
                throw new ResourcePlanningException("event with id '" + eventId + "' could not be found!!");
            }
            if (!(event.getEventState().equals(EventState.PLANNED)))
            {
                throw new ResourcePlanningException("event must have state '" + EventState.PLANNED + "'!!");
            }
            // start request process for every active helper...
            List<Helper> activeHelpers =
                    Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
            for (Helper helper : activeHelpers)
            {
                startHelperRequestProcess(helper, event);
            }
            // update event state
            RepositoryProvider.getRepository(EventRepository.class).updateEventState(event, EventState.INITIATED);

            informAdminAboutSuccess(event);
        }
        catch (Exception e)
        {
            logger.error("error on triggering helper processes : " + e.getMessage());
            throw e;
        }
        finally
        {
            configuration.setPlanningInProgress(false);
        }
    }

    private static void informAdminAboutSuccess(Event event)
    {
        new PlanningSuccessMailTemplate(null, event, null).send(true);
    }

    public static void startHelperRequestProcess(Helper helper, Event event)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event.getId());
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, businessKey, variables);
    }

    public static void onAssignmentCancelled(Long eventId, Long positionId)
    {
        // TODO make sure this works correct...
        
        if (!(RECOVER_CANCELLED_POSITIONS))
        {
            return;
        }

        // (1) get last missed assignment for the helper and the event
        List<MissedAssignment> missedAssignments =
                RepositoryProvider.getRepository(MissedAssignmentRepository.class).findByPositionAndEvent(positionId,
                        eventId);
        if ((missedAssignments == null) || (missedAssignments.size() == 0))
        {
            // no missed assignments
            return;
        }

        // (2) process every missed assignment
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        for (MissedAssignment missedAssignment : missedAssignments)
        {                        
            new PositionRecoveryOnCancellationMailTemplate(missedAssignment.getHelper(), event, position).send(false);
        }
   }
}