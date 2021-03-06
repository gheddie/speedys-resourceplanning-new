package de.trispeedys.resourceplanning.webservice;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.Task;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.dto.EventDTO;
import de.trispeedys.resourceplanning.dto.ExecutionDTO;
import de.trispeedys.resourceplanning.dto.HelperDTO;
import de.trispeedys.resourceplanning.dto.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.dto.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.dto.MessageDTO;
import de.trispeedys.resourceplanning.dto.PositionDTO;
import de.trispeedys.resourceplanning.dto.RequestedSwapDTO;
import de.trispeedys.resourceplanning.entity.AggregationRelation;
import de.trispeedys.resourceplanning.entity.AssignmentSwap;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.ManualAssignmentComment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.exception.ResourcePlanningSwapException;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.repository.AggregationRelationRepository;
import de.trispeedys.resourceplanning.repository.AssignmentSwapRepository;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.EventPositionRepository;
import de.trispeedys.resourceplanning.repository.GuidedEventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.ManualAssignmentCommentRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.rule.ChoosablePositionGenerator;
import de.trispeedys.resourceplanning.util.EntityTreeNode;
import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.PositionTreeNode;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.marshalling.ListMarshaller;

@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    private static final Logger logger = Logger.getLogger(ResourceInfo.class);

    private static final String EVENT_ID_REQUIRED = "EVENT_ID_REQUIRED";

    private static final String EVENT_NOT_FOUND_BY_ID = "EVENT_NOT_FOUND_BY_ID";

    private static final String WRONG_EVENT_STATE = "WRONG_EVENT_STATE";

    private static final String WRONG_EVENT_STATE_FOR_ADDING_POS = "WRONG_EVENT_STATE_FOR_ADDING_POS";

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
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        List<PositionDTO> dtos = new ArrayList<PositionDTO>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsInEvent(event, false))
        {
            dtos.add(asPositionDTO(pos));
        }
        return dtos.toArray(new PositionDTO[dtos.size()]);
    }

    private PositionDTO asPositionDTO(Position pos)
    {
        PositionDTO dto = new PositionDTO();
        dto.setDescription(pos.getDescription());
        dto.setMinimalAge(pos.getMinimalAge());
        dto.setDomain(pos.getDomain().getName());
        dto.setPositionNumber(pos.getPositionNumber());
        dto.setPositionId(pos.getId());
        return dto;
    }

    /**
     * returns all {@link Position} which are not included in the given {@link GuidedEvent}.
     * 
     * @param eventId
     * @param includedInEvent
     * @return
     */
    public PositionDTO[] queryEventPositions(Long eventId, boolean includedInEvent)
    {
        // TODO only query those positions (in case of adding) from domains which are suitable for the given event template
        // perhaps we should have different methods for adding/removing positions?

        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        List<PositionDTO> dtos = new ArrayList<PositionDTO>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findEventPositions(event, includedInEvent))
        {
            dtos.add(asPositionDTO(pos));
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
    
    public void triggerDedicatedHelperProcess(Long eventId, Long helperId)
    {
        EventManager.triggerDedicatedHelperProcess(eventId, helperId);
    }

    public void finishUp()
    {
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
    }

    public EventDTO[] queryEvents()
    {
        List<GuidedEvent> allEvents = Datasources.getDatasource(GuidedEvent.class).findAll(null);
        List<EventDTO> dtos = new ArrayList<EventDTO>();
        EventDTO dto = null;
        for (GuidedEvent event : allEvents)
        {
            dto = new EventDTO();
            dto.setDescription(event.getDescription());
            dto.setPositionCount(RepositoryProvider.getRepository(EventPositionRepository.class).findByEvent(event).size());
            // TODO nur ungek�ndigte (jetzt OK ?!?) !!
            dto.setAssignmentCount(RepositoryProvider.getRepository(HelperAssignmentRepository.class).findUncancelledByEvent(event).size());
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
        GuidedEvent event = Datasources.getDatasource(GuidedEvent.class).findById(null, eventId);
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
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
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
    
    public RequestedSwapDTO[] queryRequestedSwaps(Long eventId)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        GuidedEvent event = Datasources.getDatasource(GuidedEvent.class).findById(null, eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not found!!");
        }
        List<RequestedSwapDTO> dtos = new ArrayList<RequestedSwapDTO>();
        RequestedSwapDTO dto = null;
        for (AssignmentSwap swap : RepositoryProvider.getRepository(AssignmentSwapRepository.class).findByEvent(event))
        {
            dto = new RequestedSwapDTO();
            dto.setSourcePosition(swap.getSourcePosition().getDescription());
            dto.setSourceDomain(swap.getSourcePosition().getDomain().getName());
            dto.setTargetPosition(swap.getTargetPosition().getDescription());
            dto.setTargetDomain(swap.getTargetPosition().getDomain().getName());
            dto.setSwapType(swap.getSwapType().toString());
            dto.setSwapState(swap.getSwapState().toString());
            dto.setBusinessKey(BusinessKeys.generateSwapBusinessKey(swap.getEvent(), swap.getSourcePosition(), swap.getTargetPosition()));
            dto.setSourceHelper(swap.getSourceHelper() != null
                    ? swap.getSourceHelper().getLastName() + ", " + swap.getSourceHelper().getFirstName()
                    : "");
            dto.setTargetHelper(swap.getTargetHelper() != null
                    ? swap.getTargetHelper().getLastName() + ", " + swap.getTargetHelper().getFirstName()
                    : "");
            dtos.add(dto);
        }
        return dtos.toArray(new RequestedSwapDTO[dtos.size()]);
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

    public ManualAssignmentDTO[] queryManualAssignments(Long eventId)
    {
        List<ManualAssignmentDTO> dtos = new ArrayList<ManualAssignmentDTO>();
        ManualAssignmentDTO dto = null;
        Helper helper = null;
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        ManualAssignmentComment wishFound = null;
        for (Task manualAssignmentTask : BpmPlatform.getDefaultProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskDefinitionKey(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                .list())
        {
            ProcessInstance execution =
                    (ProcessInstance) BpmPlatform.getDefaultProcessEngine().getRuntimeService().createExecutionQuery().executionId(manualAssignmentTask.getExecutionId()).list().get(0);
            String businessKey = execution.getBusinessKey();

            String bkPartial = new MessageFormat("event:{0}").format(new Object[]
            {
                String.valueOf(eventId)
            });
            if (businessKey.contains(bkPartial))
            {
                // TODO is there a better to determine whether this task is a task from a process belonging to the given event ID?
                dto = new ManualAssignmentDTO();
                dto.setTaskId(manualAssignmentTask.getId());
                helper = getHelper(manualAssignmentTask);
                dto.setHelperName(helper.getLastName() + ", " + helper.getFirstName());
                wishFound = RepositoryProvider.getRepository(ManualAssignmentCommentRepository.class).findByEventAndHelper(event, helper);
                dto.setWish(wishFound != null
                        ? wishFound.getComment()
                        : "");
                dtos.add(dto);
            }
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
        RuntimeService runtimeService = BpmPlatform.getDefaultProcessEngine().getRuntimeService();
        for (Execution execution : runtimeService.createExecutionQuery().messageEventSubscriptionName(messageName).list())
        {
            List<VariableInstance> variables =
                    runtimeService.createVariableInstanceQuery().processInstanceIdIn(execution.getProcessInstanceId()).variableName(BpmVariables.RequestHelpHelper.VAR_HELPER_ID).list();
            Long helperId = (Long) variables.get(0).getValue();
            helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
            if (helper != null)
            {
                dto = new ExecutionDTO();
                dto.setHelperFirstName(helper.getFirstName());
                dto.setHelperLastName(helper.getLastName());
                dto.setWaitState(waitState);
                switch (messageName)
                {
                    case BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK:
                        dto.setAdditionalInfo(String.valueOf(runtimeService.createVariableInstanceQuery()
                                .processInstanceIdIn(execution.getProcessInstanceId())
                                .variableName(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS)
                                .list()
                                .get(0)
                                .getValue()));
                        break;
                    default:
                        dto.setAdditionalInfo(null);
                        break;
                }
                dtos.add(dto);   
            }
            else
            {
                System.out.println("helper for id '"+helperId+"' could not be found -> unable to add execution dto!!!");
            }
        }
        return dtos;
    }

    private Helper getHelper(Task task)
    {
        VariableInstanceQuery qry =
                BpmPlatform.getDefaultProcessEngine().getRuntimeService().createVariableInstanceQuery().executionIdIn(task.getExecutionId()).variableName(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
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
            businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
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
        // TODO add parameter 'internal' here, or webservice can only create internal helpers...
        Helper helper = EntityFactory.buildHelper(lastName, firstName, email, HelperState.ACTIVE, dayOfBirth, monthOfBirth, yearOfBirth, true).saveOrUpdate();
        // start a request help process for every event which is initiated in this moment...
        for (GuidedEvent event : RepositoryProvider.getRepository(GuidedEventRepository.class).findInitiatedEvents())
        {
            // TODO a confirmation mail for the helper would be cool...
            EventManager.startHelperRequestProcess(helper, event);
        }
    }

    public Long createDomain(String name, int domainNumber)
    {
        return RepositoryProvider.getRepository(DomainRepository.class).createDomain(name, domainNumber).getId();
    }

    public void createPosition(String description, int positionNumber, Long domainId, int minimalAge, boolean choosable)
    {
        if (domainId == null)
        {
            throw new ResourcePlanningException("domain id must not be null!!");
        }
        Domain domain = RepositoryProvider.getRepository(DomainRepository.class).findById(domainId);
        if (domain == null)
        {
            throw new ResourcePlanningException("domain with id '" + domainId + "' could not found!!");
        }
        RepositoryProvider.getRepository(PositionRepository.class).createPosition(description, positionNumber, domain, minimalAge, choosable);
    }
    
    public void killSwap(Long positionIdSource, Long positionIdTarget, Long eventId)
    {
        String businessKey = BusinessKeys.generateSwapBusinessKey(eventId, positionIdSource, positionIdTarget);
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_KILL_SWAP, businessKey);
    }
    
    public void killSwapByBusinessKey(String swapBusinessKey)
    {
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().correlateMessage(BpmMessages.Swap.MSG_KILL_SWAP, swapBusinessKey);
    }

    public void swapPositions(Long positionIdSource, Long positionIdTarget, Long eventId, boolean swapBySystem)
    {
        /**
         * TODO check if the helpers are assignable to new position (simple and complex). it is checked later on (PerformSwapDelegate),
         * but that might be too late (potentially leaves a process in an undefined state). 
         */
        
        if ((positionIdSource == null) || (positionIdTarget == null))
        {
            throw new ResourcePlanningException("both source and target position id must be set!");
        }
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        if (!(event.getEventState().equals(EventState.INITIATED)))
        {
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE, EventState.INITIATED));
        }
        
        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        
        Position source = positionRepository.findById(positionIdSource);
        if (source == null)
        {
            throw new ResourcePlanningException("source position for id '"+positionIdSource+"' could not be found!");
        }
        Position target = positionRepository.findById(positionIdTarget);
        if (target == null)
        {
            throw new ResourcePlanningException("target position for id '"+positionIdTarget+"' could not be found!");
        }
        // start a process in order to swap positions...            
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.Swap.VAR_SWAP_BY_SYSTEM, swapBySystem);        
        variables.put(BpmVariables.Swap.VAR_POS_ID_SOURCE, source.getId());        
        variables.put(BpmVariables.Swap.VAR_POS_ID_TARGET, target.getId());
        Helper sourceHelper = getConfirmedHelper(event, source);
        variables.put(BpmVariables.Swap.VAR_HELPER_ID_SOURCE, sourceHelper.getId());        
        Helper targetHelper = getConfirmedHelper(event, target);
        if (targetHelper != null)
        {
            // there is no target helper in a simple swap...
            variables.put(BpmVariables.Swap.VAR_HELPER_ID_TARGET, targetHelper.getId());   
        }
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, eventId);
        String businessKey = BusinessKeys.generateSwapBusinessKey(event, source, target);
        
        // check for collisions
        AssignmentSwap swap = EntityFactory.buildAssignmentSwap(event, source, target, null, null, null, null);
        for (AssignmentSwap requestedSwap : RepositoryProvider.getRepository(AssignmentSwapRepository.class).findRequestedByEvent(event))
        {
            if (swap.collidesWith(requestedSwap))
            {
                throw new ResourcePlanningSwapException("swap collides with another swap of state 'REQUESTED'!");
            }
        }
        
        // no collision --> fire process...
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByMessage(BpmMessages.Swap.MSG_START_SWAP, businessKey, variables);
    }

    private Helper getConfirmedHelper(GuidedEvent event, Position position)
    {
        HelperAssignment confirmedAssignment = RepositoryProvider.getRepository(HelperAssignmentRepository.class).findConfirmedByPositionAndEvent(event, position);
        return (confirmedAssignment != null ? confirmedAssignment.getHelper() : null);
    }

    public void removePositionsFromEvent(Long eventId, String positionNumbers)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        // event must be 'PLANNED'
        if (!(event.getEventState().equals(EventState.PLANNED)))
        {
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE, EventState.PLANNED));
        }
        List<Integer> posNumbers = ListMarshaller.unmarshall(positionNumbers);
        if ((posNumbers == null) || (posNumbers.size() == 0))
        {
            return;
        }
        for (Integer posNumber : posNumbers)
        {
            // TODO do this in a dedicated transaction!!
            removePositionFromEvent(event, posNumber);
        }
    }

    private void removePositionFromEvent(GuidedEvent event, int positionNumber)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
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

    public void addPositionsToEvent(Long eventId, String positionNumbers)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        if (eventId == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_ID_REQUIRED));
        }
        GuidedEvent event = RepositoryProvider.getRepository(GuidedEventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException(configuration.getText(this, EVENT_NOT_FOUND_BY_ID, eventId));
        }
        
        if ((event.getEventState().equals(EventState.PLANNED)) || (event.getEventState().equals(EventState.INITIATED)))
        {
            // corrent state --> do it!!
            List<Integer> posNumbers = ListMarshaller.unmarshall(positionNumbers);
            for (Integer posNumber : posNumbers)
            {
                // TODO do this in a dedicated transaction!!
                addPositionToEvent(event, posNumber);
            }
        }
        else
        {
            // wrong state --> raise exception!
            throw new ResourcePlanningException(configuration.getText(this, WRONG_EVENT_STATE_FOR_ADDING_POS, EventState.PLANNED, EventState.INITIATED));
        }
    }
    
    public void refreshConfigurations()
    {
        AppConfiguration.getInstance().parseConfiguration();
    }

    private void addPositionToEvent(GuidedEvent event, int positionNumber)
    {
        AppConfiguration configuration = AppConfiguration.getInstance();

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