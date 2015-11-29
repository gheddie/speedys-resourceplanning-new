package de.trispeedys.resourceplanning.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.comparator.EnumeratedEventItemComparator;
import de.trispeedys.resourceplanning.util.comparator.TreeNodeComparator;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class SpeedyRoutines
{
    public static Event duplicateEvent(Event event, String description, String eventKey, int day, int month, int year,
            List<Integer> positionExcludes, List<PositionInclude> includes)
    {
        if (event == null)
        {
            return null;
        }
        if (!(event.getEventState().equals(EventState.FINISHED)))
        {
            throw new ResourcePlanningException("only a finished event can be duplicated!!");
        }
        checkExcludes(event, positionExcludes);
        Event newEvent =
                EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED, event.getEventTemplate(),
                        null).saveOrUpdate();
        List<EventPosition> posRelations = Datasources.getDatasource(EventPosition.class).find("event", event);
        Position pos = null;
        for (EventPosition evtpos : posRelations)
        {
            pos = evtpos.getPosition();
            // attach every old position to the new event
            if (!(excludePosition(pos, positionExcludes)))
            {
                EntityFactory.buildEventPosition(newEvent, pos).saveOrUpdate();
            }
        }
        // process includes
        if (includes != null)
        {
            Position additionalPosition = null;
            for (PositionInclude include : includes)
            {
                additionalPosition =
                        RepositoryProvider.getRepository(PositionRepository.class).findPositionByPositionNumber(
                                include.getPositionNumber());
                if (additionalPosition == null)
                {
                    throw new ResourcePlanningException("unable to find position by position number '" +
                            include.getPositionNumber() + "'!!");
                }
                EntityFactory.buildEventPosition(newEvent, additionalPosition).saveOrUpdate();
            }
        }
        return newEvent;
    }

    private static void checkExcludes(Event event, List<Integer> positionExcludes)
    {
        if ((positionExcludes == null) || (positionExcludes.size() == 0))
        {
            return;
        }
        // list with all pos numbers in the given event...
        List<Integer> posNumbersInEvent = new ArrayList<Integer>();
        for (Position pos : RepositoryProvider.getRepository(PositionRepository.class).findPositionsInEvent(event))
        {
            posNumbersInEvent.add(pos.getPositionNumber());
        }
        for (Integer exclude : positionExcludes)
        {
            if (!(posNumbersInEvent.contains(exclude)))
            {
                throw new ResourcePlanningException("pos number '" +
                        exclude + "' can not be excluded from event as it is not present!!");
            }
        }
    }

    private static boolean excludePosition(Position position, List<Integer> positionExcludes)
    {
        return ((positionExcludes != null) && (positionExcludes.contains(position.getPositionNumber())));
    }

    public static String createHelperCode(Helper helper)
    {
        if (helper == null)
        {
            throw new ResourcePlanningException("helper must not be NULL!!");
        }
        if ((StringUtil.isBlank(helper.getLastName())) || (helper.getLastName().length() < 2))
        {
            throw new ResourcePlanningException("helpers last name must be at least 2 digits long!!");
        }
        if ((StringUtil.isBlank(helper.getFirstName())) || (helper.getFirstName().length() < 2))
        {
            throw new ResourcePlanningException("helpers first name must be at least 2 digits long!!");
        }        
        Calendar cal = Calendar.getInstance();
        cal.setTime(helper.getDateOfBirth());
        StringBuffer result = new StringBuffer();
        result.append(helper.getLastName()
                .substring(0, 2)
                .toUpperCase()
                .replaceAll("Ä", "A")
                .replaceAll("Ö", "O")
                .replaceAll("Ü", "U"));
        result.append(helper.getFirstName()
                .substring(0, 2)
                .toUpperCase()
                .replaceAll("Ä", "A")
                .replaceAll("Ö", "O")
                .replaceAll("Ü", "U"));
        String dayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        result.append(dayStr.length() == 2
                ? dayStr
                : "0" + dayStr);
        String monthStr = String.valueOf(cal.get(Calendar.MONTH) + 1);
        result.append(monthStr.length() == 2
                ? monthStr
                : "0" + monthStr);
        result.append(cal.get(Calendar.YEAR));
        return result.toString();
    }

    public static void relatePositionsToEvent(Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildEventPosition(event, position).saveOrUpdate();
        }
    }
    
    public static void createPositionAggregation(String groupName, Position... positions)
    {
        if (StringUtil.isBlank(groupName))
        {
            throw new ResourcePlanningException("group name must be set for creating a position aggregation!!");
        }
        if ((positions == null) || (positions.length == 0))
        {
            throw new ResourcePlanningException("at least one position must be provided for creating a position aggregation!!");
        }   
        PositionAggregation aggregation = EntityFactory.buildPositionAggregation(groupName).saveOrUpdate();
        for (Position pos : positions)
        {
            if (pos == null)
            {
                throw new ResourcePlanningException("can not build a position aggregation on a NULL position!!");
            }
            EntityFactory.buildAggregationRelation(pos, aggregation).saveOrUpdate();
        }
    }

    public static void relateEventsToPosition(Position position, Event... events)
    {
        for (Event event : events)
        {
            EntityFactory.buildEventPosition(event, position).saveOrUpdate();
        }
    }

    public static void assignHelperToPositions(Helper helper, Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate();
        }
    }
    
    public static void confirmHelperToPositions(Helper helper, Event event, Position... positions)
    {
        for (Position position : positions)
        {
            EntityFactory.buildHelperAssignment(helper, event, position, HelperAssignmentState.CONFIRMED).saveOrUpdate();
        }        
    }

    public static EntityTreeNode eventAsTree(Long eventId, boolean onlyUnassigned)
    {
        return eventAsTree(RepositoryProvider.getRepository(EventRepository.class).findById(eventId), onlyUnassigned);
    }

    public static EntityTreeNode eventAsTree(Event event, boolean onlyUnassigned)
    {
        if (event == null)
        {
            return null;
        }
        List<HelperAssignment> helperAssignments =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class).findByEvent(event);
        HashMap<Long, HelperAssignment> assignmentMap = new HashMap<Long, HelperAssignment>();
        for (HelperAssignment assignment : helperAssignments)
        {
            if (!(assignment.isCancelled()))
            {
                // ignore cancelled asignments here...
                assignmentMap.put(assignment.getPosition().getId(), assignment);
            }
        }
        HashMap<Domain, List<Position>> positionsPerDomain = new HashMap<Domain, List<Position>>();
        EntityTreeNode<Event> eventNode = new EventTreeNode<Event>();
        eventNode.setPayLoad(event);
        Domain key = null;
        List<EventPosition> eventPositions = event.getEventPositions();
        if ((eventPositions == null) || (eventPositions.size() == 0))
        {
            return eventNode;
        }
        for (EventPosition pos : eventPositions)
        {
            key = pos.getPosition().getDomain();
            if (positionsPerDomain.get(key) == null)
            {
                positionsPerDomain.put(key, new ArrayList<Position>());
            }
            positionsPerDomain.get(key).add(pos.getPosition());
        }
        EntityTreeNode<Domain> domainNode = null;
        // build tree
        EnumeratedEventItemComparator itemComparator = new EnumeratedEventItemComparator();
        List<EntityTreeNode<Domain>> domainNodes = new ArrayList<EntityTreeNode<Domain>>();
        for (Domain dom : positionsPerDomain.keySet())
        {
            domainNode = new DomainTreeNode<Domain>();
            domainNode.setPayLoad(dom);
            List<Position> positionList = positionsPerDomain.get(dom);
            // sort positions
            Collections.sort(positionList, itemComparator);
            for (Position pos : positionList)
            {
                PositionTreeNode<Position> childPosNode = new PositionTreeNode<Position>();
                childPosNode.setPayLoad(new AssignmentContainer(pos, getAssignment(assignmentMap, pos)));
                domainNode.acceptChild(childPosNode);
            }
            domainNodes.add(domainNode);
        }
        // add domain nodes to root (sort domains before)...
        Collections.sort(domainNodes, new TreeNodeComparator());
        for (Object domNode : domainNodes)
        {
            eventNode.acceptChild(domNode);
        }
        // return result
        return eventNode;
    }

    private static Helper getAssignment(HashMap<Long, HelperAssignment> assignmentMap, Position pos)
    {
        return (assignmentMap.get(pos.getId()) != null
                ? assignmentMap.get(pos.getId()).getHelper()
                : null);
    }

    public static List<EntityTreeNode> flattenedEventNodes(Event event, boolean onlyUnassigned)
    {
        EntityTreeNode<Event> root = eventAsTree(event, onlyUnassigned);
        return flattenedEventNodesRecursive(root, new ArrayList<EntityTreeNode>());
    }

    private static List<EntityTreeNode> flattenedEventNodesRecursive(EntityTreeNode root, List<EntityTreeNode> nodes)
    {
        nodes.add(root);
        if ((root.getChildren() != null) && (root.getChildren().size() > 0))
        {
            for (Object child : root.getChildren())
            {
                flattenedEventNodesRecursive((EntityTreeNode) child, nodes);
            }
        }
        return nodes;
    }

    public static List<AbstractDbObject> flattenedEventTree(Event event, boolean onlyUnassigned)
    {
        EntityTreeNode<Event> root = eventAsTree(event, onlyUnassigned);
        return flattenedEventTreeRecursive(root, new ArrayList<AbstractDbObject>());
    }

    private static List<AbstractDbObject> flattenedEventTreeRecursive(EntityTreeNode root, List<AbstractDbObject> values)
    {
        values.add((AbstractDbObject) root.getHierarchicalItem());
        if ((root.getChildren() != null) && (root.getChildren().size() > 0))
        {
            for (Object child : root.getChildren())
            {
                flattenedEventTreeRecursive((EntityTreeNode) child, values);
            }
        }
        return values;
    }

    public static void debugEvent(Event event)
    {
        System.out.println(" ---------- EVENT --------------------------------- ");
        EntityTreeNode node = null;
        for (EntityTreeNode object : flattenedEventNodes(event, false))
        {
            node = (EntityTreeNode) object;
            switch (node.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    System.out.println(" + " + node.infoString());
                    break;
                case HierarchicalEventItem.LEVEL_DOMAIN:
                    System.out.println("   + " + node.infoString());
                    break;
                case HierarchicalEventItem.LEVEL_POSITION:
                    System.out.println("      + " + node.infoString());
                    break;
            }
        }
        System.out.println(" ---------- EVENT --------------------------------- ");
    }

    public static String eventOutline(Event event)
    {
        String result = "";
        for (AbstractDbObject obj : flattenedEventTree(event, false))
        {
            result += ((HierarchicalEventItem) obj).getDifferentiator();
        }
        return result;
    }

    public static void parseEvent(Document document)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try
        {
            tx = session.beginTransaction();            
            EventPosition evtpos = null;
            PositionRepository repository = RepositoryProvider.getRepository(PositionRepository.class);
            Event event =
                    EntityFactory.buildEvent(
                            "1",
                            "2",
                            1,
                            1,
                            1980,
                            EventState.PLANNED,
                            (EventTemplate) Datasources.getDatasource(EventTemplate.class).findSingle(
                                    EventTemplate.ATTR_DESCRIPTION, "TRI"), null);
            // positions
            NodeList positionNodeList = document.getElementsByTagName("eventposition");
            Node positionNode = null;
            for (int positionIndex = 0; positionIndex < positionNodeList.getLength(); positionIndex++)
            {
                positionNode = positionNodeList.item(positionIndex);
                int positionNumber = Integer.parseInt(positionNode.getAttributes().getNamedItem("pos").getTextContent());
                System.out.println(positionNumber);
                session.save(event);
                evtpos = EntityFactory.buildEventPosition(event, repository.findPositionByPositionNumber(positionNumber));
                session.save(evtpos);
            }
            // positions
            NodeList assignmentNodeList = document.getElementsByTagName("assignment");
            Node assignmentNode = null;
            for (int assignmentIndex = 0; assignmentIndex < assignmentNodeList.getLength(); assignmentIndex++)
            {
                assignmentNode = assignmentNodeList.item(assignmentIndex);
                System.out.println("@@@" + assignmentNode.getAttributes().getNamedItem("pos"));
                System.out.println(assignmentNode.getAttributes().getNamedItem("helper"));
                // evtpos = EntityFactory.buildHelperAssignment(helper, event, position, helperAssignmentState);
            }            
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            tx.rollback();
        }
        finally
        {
            session.close();
        }
    }
}