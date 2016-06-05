package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.trispeedys.resourceplanning.BpmHelper;
import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MissedAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.helprequest.PlanningSuccessMailTemplate;
import de.trispeedys.resourceplanning.messaging.template.helprequest.PositionRecoveryOnCancellationMailTemplate;
import de.trispeedys.resourceplanning.repository.GuidedEventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MissedAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.util.StringUtil;

public class EventManager
{
    private static final Logger logger = Logger.getLogger(EventManager.class);

    private static final String PLANNING_IN_PROGRESS = "PLANNING_IN_PROGRESS";

    public static void triggerHelperProcesses(String templateName)
    {
        if (StringUtil.isBlank(templateName))
        {
            throw new ResourcePlanningException("template name must not be blank!!");
        }
        List<GuidedEvent> events = RepositoryProvider.getRepository(GuidedEventRepository.class).findEventsByTemplateAndStatus(templateName, EventState.PLANNED);
        if ((events == null) || (events.size() != 1))
        {
            throw new ResourcePlanningException("there must be exactly one planned event of template '" + templateName + "'!!");
        }
        GuidedEvent event = events.get(0);
        if (!(event.getEventState().equals(EventState.PLANNED)))
        {
            throw new ResourcePlanningException("event must have state '" + EventState.PLANNED + "'!!");
        }
        // start request process for every active helper...
        List<Helper> activeHelpers = Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : activeHelpers)
        {
            startHelperRequestProcess(helper, event);
        }
        // update event state
        RepositoryProvider.getRepository(GuidedEventRepository.class).updateEventState(event, EventState.INITIATED);

        informAdminAboutSuccess(event);
    }
    
    public static void triggerDedicatedHelperProcess(Long eventId, Long helperId)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not be found!!");
        }
        // processes can be restarted on 'PLANNED' and 'INITIATED' events...
        if ((!(event.getEventState().equals(EventState.PLANNED))) && (!(event.getEventState().equals(EventState.INITIATED))))
        {
            throw new ResourcePlanningException("event must have state '" + EventState.PLANNED + "' or '" + EventState.INITIATED + "'!!");
        }
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        if (helper == null)
        {
            throw new ResourcePlanningException("helper with id '" + helperId + "' could not be found!!");
        }
        if (!(helper.getHelperState().equals(HelperState.ACTIVE)))
        {
            throw new ResourcePlanningException("helper state must be '" + HelperState.ACTIVE + "'!!");
        }
        // make sure there is no process with the generated business key,
        // so --> no active process for the combination of given helper and event
        List<ProcessInstance> instances =
                BpmPlatform.getDefaultProcessEngine()
                        .getRuntimeService()
                        .createProcessInstanceQuery()
                        .processInstanceBusinessKey(BusinessKeys.generateRequestHelpBusinessKey(helper, event))
                        .list();
        if ((instances != null) && (instances.size() > 0))
        {
            throw new ResourcePlanningException("there already is a running fpr the given event and helper!!");
        }
        // finally...
        startHelperRequestProcess(helper, event);
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
            GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
            if (event == null)
            {
                throw new ResourcePlanningException("event with id '" + eventId + "' could not be found!!");
            }
            if (!(event.getEventState().equals(EventState.PLANNED)))
            {
                throw new ResourcePlanningException("event must have state '" + EventState.PLANNED + "'!!");
            }
            // start request process for every active helper...
            List<Helper> activeHelpers = Datasources.getDatasource(Helper.class).find(null, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
            for (Helper helper : activeHelpers)
            {
                startHelperRequestProcess(helper, event);
            }
            // update event state
            RepositoryProvider.getRepository(GuidedEventRepository.class).updateEventState(event, EventState.INITIATED);

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

    private static void informAdminAboutSuccess(GuidedEvent event)
    {
        new PlanningSuccessMailTemplate(null, event, null).send(true);
    }

    public static void startHelperRequestProcess(Helper helper, GuidedEvent event)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, new Long(event.getId()));
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helper, event);
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, businessKey, variables);
    }

    public static void onAssignmentCancelled(Long eventId, Long positionId)
    {
        // (1) get last missed assignment for the helper and the event
        List<MissedAssignment> missedAssignments = RepositoryProvider.getRepository(MissedAssignmentRepository.class).findUnusedByPositionAndEvent(positionId, eventId);
        if ((missedAssignments == null) || (missedAssignments.size() == 0))
        {
            // no missed assignments
            return;
        }
        // (2) process every missed assignment...
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findById(positionId);
        for (MissedAssignment missedAssignment : missedAssignments)
        {
            // ...if the helpers process can correlate the message at this point of time.
            if (BpmHelper.checkMessageSubscription(event.getId(), missedAssignment.getHelperId(), BpmMessages.RequestHelpHelper.MSG_ASSIG_RECOVERY, null))
            {
                new PositionRecoveryOnCancellationMailTemplate(missedAssignment.getHelper(), event, position).send(false);
                missedAssignment.setUsed(true);
                RepositoryProvider.getRepository(MissedAssignmentRepository.class).saveOrUpdate(missedAssignment);   
            }
        }
    }
}