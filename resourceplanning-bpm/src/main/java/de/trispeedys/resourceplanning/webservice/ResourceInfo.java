package de.trispeedys.resourceplanning.webservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.hibernate.Transaction;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.dto.EventDTO;
import de.trispeedys.resourceplanning.dto.ExecutionDTO;
import de.trispeedys.resourceplanning.dto.HelperDTO;
import de.trispeedys.resourceplanning.dto.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.dto.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.dto.MessageDTO;
import de.trispeedys.resourceplanning.dto.PositionDTO;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.persistence.SessionHolder;
import de.trispeedys.resourceplanning.persistence.SessionManager;
import de.trispeedys.resourceplanning.repository.AggregationRelationRepository;
import de.trispeedys.resourceplanning.repository.EventPositionRepository;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.rule.ChoosablePositionGenerator;
import de.trispeedys.resourceplanning.util.EntityTreeNode;
import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.PositionTreeNode;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    
    private static final Logger logger = Logger.getLogger(ResourceInfo.class);
    
    private static final String EVENT_ID_REQUIRED = "EVENT_ID_REQUIRED";

    private static final String EVENT_NOT_FOUND_BY_ID = "EVENT_NOT_FOUND_BY_ID";
    
    private static final String POSITIONS_NO_SWAP = "POSITIONS_NO_SWAP";
    
    private static final String WRONG_EVENT_STATE = "WRONG_EVENT_STATE";
    
    private static final String CANCELLATION_UNPROCESSABLE = "CANCELLATION_UNPROCESSABLE";
    
    private static final String PROC_INST_MISMATCH = "PROC_INST_MISMATCH";

    private static final String POSITION_UNAVAILABLE_BY_NUMBER = "POSITION_UNAVAILABLE_BY_NUMBER";

    private static final String POSITION_ALREADY_ASSIGNED_TO_EVENT = "POSITION_ALREADY_ASSIGNED_TO_EVENT";
    
    private static final String POSITION_NOT_ASSIGNED_TO_EVENT = "POSITION_NOT_ASSIGNED_TO_EVENT";
    
    private static final String POSITION_ASSIGNED_TO_HELPER = "POSITION_ASSIGNED_TO_HELPER";

    private static final String MAILSENDING_IN_PROGRESS = "MAILSENDING_IN_PROGRESS";

    public PositionDTO[] queryAvailablePositions(Long eventId)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
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
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (configuration.isMailSendingInProgress())
        {
            // sending in progress --> exception
            throw new ResourcePlanningException(configuration.getText(this, MAILSENDING_IN_PROGRESS));
        }
        
        try
        {
            configuration.setMailSendingInProgress(true);
            RepositoryProvider.getRepository(MessageQueueRepository.class).sendAllUnprocessedMessages();   
        }
        catch (Exception e)
        {
            logger.error("error on sending helper mails : " + e.getMessage());
            throw e;
        }       
        finally
        {
            configuration.setMailSendingInProgress(false);
        }        
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
        List<Event> allEvents = Datasources.getDatasource(Event.class).findAll(null);
        List<EventDTO> dtos = new ArrayList<EventDTO>();
        EventDTO dto = null;
        for (Event event : allEvents)
        {
            dto = new EventDTO();
            dto.setDescription(event.getDescription());
            dto.setPositionCount(RepositoryProvider.getRepository(EventPositionRepository.class).findByEvent(event).size());
            dto.setAssignmentCount(RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEvent(event).size());
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
        Event event = Datasources.getDatasource(Event.class).findById(null, eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not found!!");
        }
        SpeedyRoutines.duplicateEvent(event, description, eventKey, day, month, year, null, null);
    }

    @SuppressWarnings("rawtypes")
    public HierarchicalEventItemDTO[] getEventNodes(Long eventId, boolean confirmedAssignmentsOnly)
    {
        if (eventId == null)
        {
            return null;
        }
        logger.info("getting event nodes...");
        // key : position id, value : aggregation relation
        HashMap<Long, AggregationRelation> relationHash = new HashMap<Long, AggregationRelation>();
        for (AggregationRelation relation : RepositoryProvider.getRepository(AggregationRelationRepository.class).findAll(null))
        {
            relationHash.put(relation.getPositionId(), relation);
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        List<Position> generatorPositions = new ChoosablePositionGenerator().generate(event);
        if (event == null)
        {
            return null;
        }
        List<EntityTreeNode> nodes = SpeedyRoutines.flattenedEventNodes(event, confirmedAssignmentsOnly);
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
            dto.setAvailability(node.getAvailability(generatorPositions, event));
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
        for (Helper helper : RepositoryProvider.getRepository(HelperRepository.class).findAll(null))
        {
            dto = new HelperDTO();
            dto.setLastName(helper.getLastName());
            dto.setFirstName(helper.getFirstName());
            dto.setEmail(helper.getEmail());
            dto.setDateOfBirth(df.format(helper.getDateOfBirth()));
            dto.setCode(helper.getCode());
            dto.setHelperState(helper.getHelperState().toString());
            dto.setHelperId(helper.getId());
            dtos.add(dto);
        }
        return dtos.toArray(new HelperDTO[dtos.size()]);
    }
    
    public MessageDTO[] queryUnsentMessages()
    {
        List<MessageDTO> dtos = new ArrayList<MessageDTO>();
        MessageDTO dto = null;
        for (MessageQueue message : RepositoryProvider.getRepository(MessageQueueRepository.class).findUnsentMessages())
        {
            dto = new MessageDTO();
            dto.setRecipient(message.getToAddress());
            dto.setSubject(message.getSubject());
            dto.setBody(message.getBody());
            dto.setMessagingState(message.getMessagingState().toString());
            dtos.add(dto);
        }
        return dtos.toArray(new MessageDTO[dtos.size()]);
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
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, "Option w�hlen"));
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, "Position w�hlen"));
        dtos.addAll(queryExecutionsByMessageName(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED, "�bernahme Planung"));
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
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, PROC_INST_MISMATCH, helperId, task.getId()));
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
            throw new ResourcePlanningException(AppConfiguration.getInstance().getText(this, CANCELLATION_UNPROCESSABLE, helperId));            
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
    
    public void swapPositions(Long helperIdSource, Long helperIdTarget, Long eventId)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();        
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }                
        if (!(event.getEventState().equals(EventState.INITIATED)))
        {
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE, EventState.INITIATED));
        }        
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);

        HelperAssignment assignmentSource = repository.findByHelperAndEvent(RepositoryProvider.getRepository(HelperRepository.class).findById(helperIdSource), event);
        HelperAssignment assignmentTarget = repository.findByHelperAndEvent(RepositoryProvider.getRepository(HelperRepository.class).findById(helperIdTarget), event);
        
        Position posSource = assignmentSource.getPosition();
        Position posTarget = assignmentTarget.getPosition();
        
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this);
        Transaction tx = null;        
        try
        {
            tx = sessionHolder.beginTransaction();
            assignmentSource.setPosition(posTarget);
            sessionHolder.saveOrUpdate(assignmentSource);
            assignmentTarget.setPosition(posSource);
            sessionHolder.saveOrUpdate(assignmentTarget);
            tx.commit();   
        }
        catch (Exception e)
        {
            tx.rollback();
            throw new ResourcePlanningException(configuration.getText(this, POSITIONS_NO_SWAP, e.getMessage()));
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);   
        }
    }
    
    public void removePositionFromEvent(Long eventId, int positionNumber)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        // event must be 'INITIATED'
        if (!(event.getEventState().equals(EventState.INITIATED)))
        {
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE, EventState.INITIATED));
        }
        // position must be there
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber(positionNumber);
        if (position == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, POSITION_UNAVAILABLE_BY_NUMBER, positionNumber));
        }
        // position must be an event position in the given event
        EventPosition eventPosition = RepositoryProvider.getRepository(EventPositionRepository.class).findByEventAndPositionNumber(event, position);
        if (eventPosition == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, POSITION_NOT_ASSIGNED_TO_EVENT, positionNumber, event.getDescription()));
        }
        // position must NOT be assigned in the event
        if (RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEventAndPosition(event, position) != null)
        {
            throw new ResourcePlanningException(configuration.getText(this, POSITION_ASSIGNED_TO_HELPER, positionNumber, event.getDescription()));
        }
        // finally, remove the event position...
        eventPosition.remove();
    }
    
    public void addPositionToEvent(Long eventId, int positionNumber)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        // event must be 'INITIATED'
        if (!(event.getEventState().equals(EventState.INITIATED)))
        {
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE, EventState.INITIATED));
        }
        // position must be there
        Position position = RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber(positionNumber);
        if (position == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, POSITION_UNAVAILABLE_BY_NUMBER, positionNumber));
        }
        // position must not already be an event position in the given event
        if (RepositoryProvider.getRepository(EventPositionRepository.class).findByEventAndPositionNumber(event, position) != null)
        {
            throw new ResourcePlanningException(configuration.getText(this, POSITION_ALREADY_ASSIGNED_TO_EVENT, positionNumber, event.getDescription()));
        }
        // finally, create the event position...
        EntityFactory.buildEventPosition(event, position).saveOrUpdate(null);
    }
}