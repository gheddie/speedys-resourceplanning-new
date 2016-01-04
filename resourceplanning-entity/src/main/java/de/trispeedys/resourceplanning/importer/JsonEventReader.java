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

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.PositionAggregation;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.TestUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class JsonEventReader
{
    private static final Logger logger = Logger.getLogger(JsonEventReader.class);

    private static final String HELPER_UNASSIGNABLE = "HELPER_UNASSIGNABLE";

    private String resourceName;

    private SessionHolder sessionHolder;

    private HashMap<String, List<Position>> aggregationCache;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    // parameter names for header
    private static final String JSON_PARAM_HEADER_DESCRIPTION = "description";

    private static final String JSON_PARAM_HEADER_KEY = "key";

    private static final String JSON_PARAM_HEADER_TEMPLATE = "template";
    
    private static final String JSON_PARAM_HEADER_EVT_DATE = "eventDate";

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
        this.resourceName = aResourceName;
        sessionHolder = SessionManager.getInstance().registerSession(this);
        try
        {
            sessionHolder.beginTransaction();
            JSONParser parser = new JSONParser();
            Event event = null;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Object obj = parser.parse(reader);
            JSONObject root = (JSONObject) obj;
            logger.info(root);
            logger.info((String) root.get(JSON_PARAM_HEADER_DESCRIPTION));
            logger.info((String) root.get(JSON_PARAM_HEADER_KEY));
            EventTemplate template = EntityFactory.buildEventTemplate((String) root.get(JSON_PARAM_HEADER_TEMPLATE));
            sessionHolder.saveOrUpdate(template);
            event =
                    EntityFactory.buildEvent((String) root.get(JSON_PARAM_HEADER_DESCRIPTION),
                            (String) root.get(JSON_PARAM_HEADER_KEY), parseDate((String) root.get(JSON_PARAM_HEADER_EVT_DATE)), EventState.FINISHED, template, null);
            sessionHolder.saveOrUpdate(event);
            JSONArray domains = (JSONArray) root.get(JSON_PARAM_LIST_DOMAINS);
            logger.info(domains.size() + " domains.");
            JSONObject jsonDomain = null;
            Domain domain = null;
            for (Object domainObj : domains)
            {
                jsonDomain = (JSONObject) domainObj;
                logger.info((String) jsonDomain.get(JSON_PARAM_DOMAIN_NUMBER));
                logger.info((String) jsonDomain.get(JSON_PARAM_DOMAIN_NAME));
                domain =
                        EntityFactory.buildDomain((String) jsonDomain.get(JSON_PARAM_DOMAIN_NAME),
                                Integer.parseInt((String) jsonDomain.get(JSON_PARAM_DOMAIN_NUMBER)));
                sessionHolder.saveOrUpdate(domain);
                
                // create link between domain and template
                sessionHolder.saveOrUpdate(EntityFactory.buildTemplateDomain(template, domain));
                
                JSONArray assignments = (JSONArray) jsonDomain.get(JSON_PARAM_LIST_ASSIGNMENTS);
                JSONObject jsonAssignment = null;
                Helper helper = null;
                Position position = null;
                Date now = new Date();
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
                    logger.info((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER));
                    logger.info((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME));
                    logger.info((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE));
                    logger.info((String) jsonAssignment.get(JSON_PARAM_POSITION_PRIORITY));
                    position =
                            EntityFactory.buildPosition((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME),
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE)),
                                    domain, Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER)),
                                    parseBoolean((String) jsonAssignment.get(JSON_PARAM_POSITION_CHOOSABLE)),
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_POSITION_PRIORITY)));
                    sessionHolder.saveOrUpdate(position);
                    sessionHolder.saveOrUpdate(EntityFactory.buildEventPosition(event, position));
                    String lastName = (String) jsonAssignment.get(JSON_PARAM_HELPER_LAST_NAME);
                    String firstName = (String) jsonAssignment.get(JSON_PARAM_HELPER_FIRST_NAME);
                    if ((StringUtil.isBlank(firstName)) || (StringUtil.isBlank(lastName)))
                    {
                        logger.info(" ### IGNORING HELPER ### ");
                    }
                    else
                    {
                        helper =
                                EntityFactory.buildHelper(lastName, firstName,
                                        (String) jsonAssignment.get(JSON_PARAM_HELPER_MAIL), HelperState.ACTIVE,
                                        parseDate((String) jsonAssignment.get(JSON_PARAM_HELPER_BIRTHDAY)));
                        if (helper.isValid())
                        {
                            if (!(helper.isAssignableTo(position, now)))
                            {
                                throw new ResourcePlanningException("Der Helfer " +
                                        helper.getLastName() + ", " + helper.getFirstName() + " (geboren : " +
                                        helper.getDateOfBirth() + ") ist zu jung f\u00fcr die Position " +
                                        position.getDescription() + " (Mindestalter : " + position.getMinimalAge() + ")!");
                            }
                            sessionHolder.saveOrUpdate(helper);
                            sessionHolder.saveOrUpdate(EntityFactory.buildHelperAssignment(helper, event, position));
                        }
                    }

                    // cache aggregation
                    cacheAggregation(position, (String) jsonAssignment.get(JSON_PARAM_POSITION_AGG_GROUP));
                }
            }

            createAggregations();

            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
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

    private Date parseDate(String dateOfBirth) throws ParseException
    {
        return DATE_FORMAT.parse(dateOfBirth);
    }

    // ---

    public static void main(String[] args)
    {
        TestUtil.clearAll();
        new JsonEventReader().doImport("TestEvent.json");
    }
}