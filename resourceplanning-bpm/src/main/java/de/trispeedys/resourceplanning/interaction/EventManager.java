package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EventManager
{
    public static void triggerHelperProcesses(String templateName)
    {
        if (StringUtil.isBlank(templateName))
        {
            throw new ResourcePlanningException("template name must not be blank!!");
        }
        List<Event> events =
                RepositoryProvider.getRepository(EventRepository.class).findEventsByTemplateAndStatus(
                        templateName, EventState.PLANNED);
        if ((events == null) || (events.size() != 1))
        {
            throw new ResourcePlanningException("there must be exactly one planned event of template '" +
                    templateName + "'!!");
        }
        Event event = events.get(0);
        if (!(event.getEventState().equals(EventState.PLANNED)))
        {
            throw new ResourcePlanningException("event must have state '"+EventState.PLANNED+"'!!");
        }
        // start request process for every active helper...
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : activeHelpers)
        {
            startHelperRequestProcess(helper, event);
        }
        // update event state
        RepositoryProvider.getRepository(EventRepository.class).updateEventState(event, EventState.INITIATED);
    }

    public static void triggerHelperProcesses(Long eventId)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        Event event = Datasources.getDatasource(Event.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not be found!!");
        }
        if (!(event.getEventState().equals(EventState.PLANNED)))
        {
            throw new ResourcePlanningException("event must have state '"+EventState.PLANNED+"'!!");
        }        
        // start request process for every active helper...
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : activeHelpers)
        {
            startHelperRequestProcess(helper, event);
        }
        // update event state
        RepositoryProvider.getRepository(EventRepository.class).updateEventState(event, EventState.INITIATED);
    }

    public static void startHelperRequestProcess(Helper helper, Event event)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event.getId());
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, businessKey,
                        variables);
    }
}