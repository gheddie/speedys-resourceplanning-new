package de.trispeedys.resourceplanning.importer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.persistence.SessionHolder;
import de.trispeedys.resourceplanning.persistence.SessionManager;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class JsonEventReader
{
    private String resourceName;

    private SessionHolder sessionHolder;

    private HashMap<String, List<Position>> aggregationCache;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    // parameter names for header
    private static final String JSON_PARAM_HEADER_DESCRIPTION = "description";

    private static final String JSON_PARAM_HEADER_KEY = "key";

    private static final String JSON_PARAM_HEADER_TEMPLATE = "template";

    // parameter names list types
    private static final String JSON_PARAM_LIST_DOMAINS = "domains";

    private static final String JSON_PARAM_LIST_ASSIGNMENTS = "assignments";

    // parameter names for domains
    private static final String JSON_PARAM_DOMAIN_NAME = "domainName";

    private static final String JSON_PARAM_DOMAIN_NUMBER = "domainNumber";

    // parameter names for positions/assignments
    private static final String JSON_PARAM_HELPER_MINIMAL_AGE = "minimalAge";

    private static final String JSON_PARAM_POSITION_PRIORITY = "positionPriority";

    private static final String JSON_PARAM_HELPER_BIRTHDAY = "birthDay";

    private static final String JSON_PARAM_POSITION_NAME = "positionName";

    private static final String JSON_PARAM_POSITION_NUMBER = "positionNumber";

    private static final String JSON_PARAM_HELPER_MAIL = "helperMail";

    private static final String JSON_PARAM_HELPER_FIRST_NAME = "firstName";

    private static final String JSON_PARAM_HELPER_LAST_NAME = "lastName";

    private static final String JSON_PARAM_POSITION_CHOOSABLE = "choosable";
    
    private static final String JSON_PARAM_POSITION_AGG_GROUP = "aggregation";

    private static final int MANDATORY_PROP_SIZE_ASSIGNMENT = 10;

    public void doImport(String aResourceName)
    {
        HibernateUtil.clearAll();
        this.resourceName = aResourceName;
        sessionHolder = SessionManager.getInstance().registerSession(this);
        Transaction tx = null;
        try
        {
            tx = sessionHolder.beginTransaction();
            JSONParser parser = new JSONParser();
            Event event = null;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Object obj = parser.parse(reader);
            JSONObject root = (JSONObject) obj;
            System.out.println(root);
            System.out.println((String) root.get(JSON_PARAM_HEADER_DESCRIPTION));
            System.out.println((String) root.get(JSON_PARAM_HEADER_KEY));
            EventTemplate template = EntityFactory.buildEventTemplate((String) root.get(JSON_PARAM_HEADER_TEMPLATE));
            sessionHolder.saveOrUpdate(template);
            event =
                    EntityFactory.buildEvent((String) root.get(JSON_PARAM_HEADER_DESCRIPTION),
                            (String) root.get(JSON_PARAM_HEADER_KEY), 1, 1, 1980, EventState.FINISHED, template, null);
            sessionHolder.saveOrUpdate(event);
            JSONArray domains = (JSONArray) root.get(JSON_PARAM_LIST_DOMAINS);
            System.out.println(domains.size() + " domains.");
            JSONObject jsonDomain = null;
            Domain domain = null;
            for (Object domainObj : domains)
            {
                jsonDomain = (JSONObject) domainObj;
                System.out.println((String) jsonDomain.get(JSON_PARAM_DOMAIN_NUMBER));
                System.out.println((String) jsonDomain.get(JSON_PARAM_DOMAIN_NAME));
                domain =
                        EntityFactory.buildDomain((String) jsonDomain.get(JSON_PARAM_DOMAIN_NAME),
                                Integer.parseInt((String) jsonDomain.get(JSON_PARAM_DOMAIN_NUMBER)));
                sessionHolder.saveOrUpdate(domain);
                JSONArray assignments = (JSONArray) jsonDomain.get(JSON_PARAM_LIST_ASSIGNMENTS);
                JSONObject jsonAssignment = null;
                Helper helper = null;
                Position position = null;
                for (Object assignmentObj : assignments)
                {
                    jsonAssignment = (JSONObject) assignmentObj;
                    if (!(jsonAssignment.entrySet().size() == MANDATORY_PROP_SIZE_ASSIGNMENT))
                    {
                        throw new ResourcePlanningException("assignment object [pos='" +
                                jsonAssignment.get("positionName") + "'] has invalid property size (" +
                                jsonAssignment.entrySet().size() + " instead of " + MANDATORY_PROP_SIZE_ASSIGNMENT +
                                ")!!");
                    }
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_PRIORITY));
                    position =
                            EntityFactory.buildPosition((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME),
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE)),
                                    domain, Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER)),
                                    parseBoolean((String) jsonAssignment.get(JSON_PARAM_POSITION_CHOOSABLE)),
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_POSITION_PRIORITY)));
                    System.out.println(helper);
                    sessionHolder.saveOrUpdate(position);
                    sessionHolder.saveOrUpdate(EntityFactory.buildEventPosition(event, position));
                    String lastName = (String) jsonAssignment.get(JSON_PARAM_HELPER_LAST_NAME);
                    String firstName = (String) jsonAssignment.get(JSON_PARAM_HELPER_FIRST_NAME);
                    if ((StringUtil.isBlank(firstName)) || (StringUtil.isBlank(lastName)))
                    {
                        System.out.println(" ### IGNORING HELPER ### ");
                    }
                    else
                    {
                        helper =
                                EntityFactory.buildHelper(lastName, firstName,
                                        (String) jsonAssignment.get(JSON_PARAM_HELPER_MAIL), HelperState.ACTIVE,
                                        parseDate((String) jsonAssignment.get(JSON_PARAM_HELPER_BIRTHDAY)));
                        if (helper.isValid())
                        {
                            sessionHolder.saveOrUpdate(helper);
                            sessionHolder.saveOrUpdate(EntityFactory.buildHelperAssignment(helper, event, position));
                        }
                    }
                    
                    // cache aggregation
                    cacheAggregation(position, (String) jsonAssignment.get(JSON_PARAM_POSITION_AGG_GROUP));
                }
            }
            
            createAggregations();
            
            tx.commit();
        }
        catch (Exception e)
        {
            tx.rollback();
            throw new ResourcePlanningException("event could not be parsed : " + e.getMessage());
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }

    private void createAggregations()
    {
        if (aggregationCache == null)
        {
            return;
        }
        PositionAggregation group = null;
        for (String key : aggregationCache.keySet())
        {
            // create group
            group = EntityFactory.buildPositionAggregation(key, true);
            sessionHolder.saveOrUpdate(group);
            for (Position pos : aggregationCache.get(key))
            {
                sessionHolder.saveOrUpdate(EntityFactory.buildAggregationRelation(pos, group));    
            }
        }
    }

    private void cacheAggregation(Position position, String groupName)
    {
        if (StringUtil.isBlank(groupName))
        {
            return;
        }
        if (aggregationCache == null)
        {
            aggregationCache = new HashMap<String, List<Position>>();
        }
        if (aggregationCache.get(groupName) == null)
        {
            aggregationCache.put(groupName, new ArrayList<Position>());
        }
        aggregationCache.get(groupName).add(position);
    }

    private boolean parseBoolean(String b)
    {   
        if (StringUtil.isBlank(b))
        {
            return false;
        }
        return (b.equals("y"));
    }

    private Date parseDate(String dateOfBirth)
    {
        try
        {
            return DATE_FORMAT.parse(dateOfBirth);
        }
        catch (ParseException e)
        {
            System.out.println(" ### ERROR ### : " + e.getMessage());
            return null;
        }
    }

    // ---

    public static void main(String[] args)
    {
        new JsonEventReader().doImport("AggregationTest.json");
    }
}