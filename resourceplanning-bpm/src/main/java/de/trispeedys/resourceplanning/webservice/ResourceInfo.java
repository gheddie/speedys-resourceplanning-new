package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.Task;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.dto.EventDTO;
import de.trispeedys.resourceplanning.dto.ExecutionDTO;
import de.trispeedys.resourceplanning.dto.HelperDTO;
import de.trispeedys.resourceplanning.dto.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.dto.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.dto.PositionDTO;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.repository.AggregationRelationRepository;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.util.EntityTreeNode;
import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.PositionTreeNode;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    private static final Logger logger = Logger.getLogger(ResourceInfo.class);

    public PositionDTO[] queryAvailablePositions(Long eventId)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not found!!");
        }
        List<PositionDTO> dtos = new ArrayList<PositionDTO>();
        PositionDTO dto = null;
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event, false))
        {
            dto = new PositionDTO();
            dto.setDescription(pos.getDescription());
            dto.setMinimalAge(pos.getMinimalAge());
            dto.setDomain(pos.getDomain().getName());
            dto.setPositionId(pos.getId());
            dtos.add(dto);
        }
        return dtos.toArray(new PositionDTO[dtos.size()]);
    }

    public void sendAllMessages()
    {
        MessagingService.sendAllUnprocessedMessages();
    }

    public void startProcessesForActiveHelpersByTemplateName(String templateName)
    {
        EventManager.triggerHelperProcesses(templateName);
    }

    public void startProcessesForActiveHelpersByEventId(Long eventId)
    {
        EventManager.triggerHelperProcesses(eventId);
    }

    public void finishUp()
    {
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
    }

    public EventDTO[] queryEvents()
    {
        List<Event> allEvents = Datasources.getDatasource(Event.class).findAll();
        List<EventDTO> dtos = new ArrayList<EventDTO>();
        EventDTO dto = null;
        for (Event event : allEvents)
        {
            dto = new EventDTO();
            dto.setDescription(event.getDescription());
            dto.setEventId(event.getId());
            dto.setEventState(event.getEventState().toString());
            dto.setEventDate(event.getEventDate());
            dtos.add(dto);
        }
        return dtos.toArray(new EventDTO[dtos.size()]);
    }

    public void duplicateEvent(Long eventId, String description, String eventKey, int day, int month, int year)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        Event event = Datasources.getDatasource(Event.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not found!!");
        }
        SpeedyRoutines.duplicateEvent(event, description, eventKey, day, month, year, null, null);
    }

    @SuppressWarnings("rawtypes")
    public HierarchicalEventItemDTO[] getEventNodes(Long eventId, boolean onlyUnassignedPositions)
    {
        logger.info("getting event nodes...");
        // key : position id, value : aggregation relation
        HashMap<Long, AggregationRelation> relationHash = new HashMap<Long, AggregationRelation>();
        for (AggregationRelation relation : RepositoryProvider.getRepository(AggregationRelationRepository.class).findAll())
        {
            relationHash.put(relation.getPositionId(), relation);
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        List<EntityTreeNode> nodes = SpeedyRoutines.flattenedEventNodes(event, onlyUnassignedPositions);
        List<HierarchicalEventItemDTO> dtos = new ArrayList<HierarchicalEventItemDTO>();
        HierarchicalEventItemDTO dto = null;
        PositionTreeNode positionNode = null;
        for (EntityTreeNode node : nodes)
        {
            dto = new HierarchicalEventItemDTO();
            dto.setItemType(node.getItemType().toString());
            dto.setInfoString(node.infoString());
            dto.setHierarchyLevel(node.getHierarchyLevel());
            dto.setItemKey(node.itemKey());
            dto.setAssignmentString(node.getAssignmentString());
            dto.setAvailability(node.getAvailability());
            dto.setPriorization(node.getPriorization());
            if (node.getItemType().equals(HierarchicalEventItemType.POSITION))
            {
                positionNode = (PositionTreeNode) node;
                dto.setGroup(relationHash.get(positionNode.getPositionId()) != null
                        ? relationHash.get(positionNode.getPositionId()).getPositionAggregation().getName()
                        : "");
            }
            dto.setEntityId(node.getEntityId());
            dtos.add(dto);
        }
        return dtos.toArray(new HierarchicalEventItemDTO[dtos.size()]);
    }

    public HelperDTO[] queryHelpers()
    {
        List<HelperDTO> dtos = new ArrayList<HelperDTO>();
        HelperDTO dto = null;
        for (Helper helper : RepositoryProvider.getRepository(HelperRepository.class).findAll())
        {
            dto = new HelperDTO();
            dto.setLastName(helper.getLastName());
            dto.setFirstName(helper.getFirstName());
            dto.setEmail(helper.getEmail());
            dto.setCode(helper.getCode());
            dto.setHelperState(helper.getHelperState().toString());
            dto.setHelperId(helper.getId());
            dtos.add(dto);
        }
        return dtos.toArray(new HelperDTO[dtos.size()]);
    }

    public ManualAssignmentDTO[] queryManualAssignments()
    {
        List<ManualAssignmentDTO> dtos = new ArrayList<ManualAssignmentDTO>();
        ManualAssignmentDTO dto = null;
        Helper helper = null;
        for (Task manualAssignmentTask : BpmPlatform.getDefaultProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskDefinitionKey(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                .list())
        {
            dto = new ManualAssignmentDTO();
            dto.setTaskId(manualAssignmentTask.getId());
            helper = getHelper(manualAssignmentTask);
            dto.setHelperName(helper.getLastName() + ", " + helper.getFirstName());
            dtos.add(dto);
        }
        return dtos.toArray(new ManualAssignmentDTO[dtos.size()]);
    }

    public ExecutionDTO[] queryExecutions()
    {
        List<ExecutionDTO> dtos = new ArrayList<ExecutionDTO>();        
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, "Option wählen"));
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, "Position wählen"));
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, "Übernahme Planung"));
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_DEACT_RESP, "Deaktivierung"));
        return dtos.toArray(new ExecutionDTO[dtos.size()]);
    }
    
    private List<ExecutionDTO> queryExecutionsByMessageName(String messageName, String waitState)
    {
        List<ExecutionDTO> dtos = new ArrayList<ExecutionDTO>();
        Helper helper = null;
        ExecutionDTO dto;
        RuntimeService runtimeService = BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService();
        for (Execution execution : runtimeService
                .createExecutionQuery()
                .messageEventSubscriptionName(messageName)
                .list())
        {
            List<VariableInstance> variables = runtimeService
                    .createVariableInstanceQuery()
                    .processInstanceIdIn(execution.getProcessInstanceId())
                    .variableName(BpmVariables.RequestHelpHelper.VAR_HELPER_ID)
                    .list();
            helper = RepositoryProvider.getRepository(HelperRepository.class).findById(
                    (Long) variables
                            .get(0)
                            .getValue());
            dto = new ExecutionDTO();
            dto.setHelperFirstName(helper.getFirstName());
            dto.setHelperLastName(helper.getLastName());
            dto.setWaitState(waitState);
            switch (messageName)
            {
                case BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK:
                    dto.setAdditionalInfo(String.valueOf(runtimeService
                            .createVariableInstanceQuery()
                            .processInstanceIdIn(execution.getProcessInstanceId())
                            .variableName(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS)
                            .list().get(0).getValue()));
                    break;
                default:
                    dto.setAdditionalInfo(null);
                    break;
            }
            dtos.add(dto);                        
        }
        return dtos;
    }

    private Helper getHelper(Task task)
    {
        VariableInstanceQuery qry =
                BpmPlatform.getDefaultProcessEngine()
                        .getRuntimeService()
                        .createVariableInstanceQuery()
                        .executionIdIn(task.getExecutionId())
                        .variableName(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        VariableInstance variableInstance = qry.list().get(0);
        Long helperId = (Long) variableInstance.getValue();
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        if (helper == null)
        {
            throw new ResourcePlanningException("got helper id '" +
                    helperId + "' from process with task id '" + task.getId() +
                    "' but NO helper from repository --> possible mismatch between camunda an speedy DB?!?");
        }
        return helper;
    }

    public void cancelAssignment(Long eventId, Long helperId)
    {
        if ((eventId == null) || (helperId == null))
        {
            return;
        }
        String businessKey = null;
        try
        {
            businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
            BpmPlatform.getDefaultProcessEngine().getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, businessKey);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            throw new ResourcePlanningException("can not correlate cancellation message [helper id=" + helperId + "]!!");
        }
    }

    public void completeManualAssignment(String taskId, Long positionId)
    {
        if ((taskId == null) || (StringUtil.isBlank(taskId)))
        {
            throw new ResourcePlanningException("task id must be set in order to complete manual assignment!!");
        }
        if (positionId == null)
        {
            throw new ResourcePlanningException("position id must be set in order to complete manual assignment!!");
        }
        System.out.println("completing manual assignment [taskId:" + taskId + "|positionId:" + positionId + "]");
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, positionId);
        BpmPlatform.getDefaultProcessEngine().getTaskService().complete(taskId, variables);
    }

    public void createHelper(String lastName, String firstName, String email, int dayOfBirth, int monthOfBirth, int yearOfBirth)
    {
        Helper helper = EntityFactory.buildHelper(lastName, firstName, email, HelperState.ACTIVE, dayOfBirth, monthOfBirth, yearOfBirth).saveOrUpdate();
        // start a request help process for every event which is initiated in this moment...
        for (Event event : RepositoryProvider.getRepository(EventRepository.class).findInitiatedEvents())
        {
            EventManager.startHelperRequestProcess(helper, event);
        }
    }
}