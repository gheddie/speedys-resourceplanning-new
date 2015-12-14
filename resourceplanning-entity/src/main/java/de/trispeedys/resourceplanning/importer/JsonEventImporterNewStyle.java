package de.trispeedys.resourceplanning.importer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.persistence.SessionHolder;
import de.trispeedys.resourceplanning.persistence.SessionManager;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class JsonEventImporterNewStyle
{
    private String resourceName;

    private SessionHolder sessionHolder;

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
    
    private static final int MANDATORY_PROP_SIZE_ASSIGNMENT = 8;

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
                    EntityFactory.buildEvent((String) root.get(JSON_PARAM_HEADER_DESCRIPTION), (String) root.get(JSON_PARAM_HEADER_KEY), 1, 1,
                            1980, EventState.FINISHED, template, null);
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
                        throw new ResourcePlanningException("assignment object [pos='"+jsonAssignment.get("positionName")+"'] has invalied property size ("+jsonAssignment.entrySet().size()+" instead of "+MANDATORY_PROP_SIZE_ASSIGNMENT+")!!");
                    }
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE));
                    System.out.println((String) jsonAssignment.get(JSON_PARAM_POSITION_PRIORITY));
                    position =
                            EntityFactory.buildPosition((String) jsonAssignment.get(JSON_PARAM_POSITION_NAME),
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_HELPER_MINIMAL_AGE)),
                                    domain,
                                    Integer.parseInt((String) jsonAssignment.get(JSON_PARAM_POSITION_NUMBER)),
                                    false);
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
                                EntityFactory.buildHelper(lastName,
                                        firstName,
                                        (String) jsonAssignment.get(JSON_PARAM_HELPER_MAIL), HelperState.ACTIVE,
                                        parseDate((String) jsonAssignment.get(JSON_PARAM_HELPER_BIRTHDAY)));
                        if (helper.isValid())
                        {
                            sessionHolder.saveOrUpdate(helper);
                            sessionHolder.saveOrUpdate(EntityFactory.buildHelperAssignment(helper, event, position));
                        }   
                    }
                }
            }
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
        new JsonEventImporterNewStyle().doImport("Helfer_2015_NewStyle.json");
    }
}