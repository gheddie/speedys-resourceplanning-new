package de.trispeedys.resourceplanning.importer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.persistence.SessionHolder;
import de.trispeedys.resourceplanning.persistence.SessionManager;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class JsonEventImporter
{
    private static final String JSON_PARAM_MINIMAL_AGE = "ma";

    private static final String JSON_PARAM_POSITION_PRIORITY = "positionPriority";

    private static final String JSON_PARAM_BIRTHDAY = "birthDay";

    private static final String JSON_PARAM_POSITION_NAME = "positionName";

    private static final String JSON_PARAM_POSITION_NUMBER = "pr";

    private static final String JSON_PARAM_DOMAIN_NAME = "domainName";

    private static final String JSON_PARAM_DOMAIN_NUMBER = "dr";

    private static final String JSON_PARAM_MAIL = "ml";

    private static final String JSON_PARAM_FIRST_NAME = "firstName";

    private static final String JSON_PARAM_LAST_NAME = "lastName";

    private List<ImportRow> rows;

    private Event event;

    private HashMap<Long, Integer> helperIdToPosNumber = new HashMap<Long, Integer>();

    private String resourceName;

    private SessionHolder sessionHolder;

    public void doImport(String aResourceName)
    {
        HibernateUtil.clearAll();
        this.resourceName = aResourceName;
        sessionHolder = SessionManager.getInstance().registerSession(this);
        Transaction tx = null;
        try
        {
            tx = sessionHolder.beginTransaction();
            EventTemplate eventTemplate = EntityFactory.buildEventTemplate("TRI");
            sessionHolder.saveOrUpdate(eventTemplate);
            event =
                    EntityFactory.buildEvent("Triathlon 2015", "TRI-2015", 1, 1, 2015, EventState.FINISHED, eventTemplate,
                            null);
            sessionHolder.saveOrUpdate(event);
            rows = parseRows();
            createHelpers();
            createPositions();
            createAssignments();
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

    private void createPositions()
    {
        HashMap<Integer, List<Position>> domainsToPositions = new HashMap<Integer, List<Position>>();
        HashMap<Integer, String> domainNumberToDomainName = new HashMap<Integer, String>();
        for (ImportRow row : rows)
        {
            if (domainsToPositions.get(row.getDomainNumber()) == null)
            {
                domainsToPositions.put(row.getDomainNumber(), new ArrayList<Position>());
            }
            if (domainNumberToDomainName.get(row.getDomainNumber()) == null)
            {
                domainNumberToDomainName.put(row.getDomainNumber(), row.getDomainName());
            }
            domainsToPositions.get(row.getDomainNumber()).add(
                    EntityFactory.buildPosition(row.getPosition(), row.getMinimalAge(), null, row.getPositionNumber(),
                            true, row.getAssignmentPriority()));
        }
        Domain persistedDomain = null;
        for (Integer domainNumber : domainsToPositions.keySet())
        {
            persistedDomain = EntityFactory.buildDomain(domainNumberToDomainName.get(domainNumber), domainNumber);
            sessionHolder.saveOrUpdate(persistedDomain);
            for (Position pos : domainsToPositions.get(domainNumber))
            {
                pos.setDomain(persistedDomain);
                sessionHolder.saveOrUpdate(pos);
                // relate position to event
                sessionHolder.saveOrUpdate(EntityFactory.buildEventPosition(event, pos));
            }
        }
    }

    private void createHelpers()
    {
        Helper buildedHelper = null;
        for (ImportRow row : rows)
        {
            if (row.isHelperValid())
            {
                try
                {
                    buildedHelper =
                            EntityFactory.buildHelper(row.getHelperLastName(), row.getHelperFirstName(),
                                    row.getHelperMail(), HelperState.ACTIVE, row.getDateOfBirth());
                    sessionHolder.saveOrUpdate(buildedHelper);
                    helperIdToPosNumber.put(buildedHelper.getId(), new Integer(row.getPositionNumber()));
                }
                catch (Exception e)
                {
                    System.out.println(" ### ERROR ### : " + e.getMessage());
                }
            }
        }
        sessionHolder.flush();
    }

    private void createAssignments()
    {
        // cache all helpers
        HashMap<Long, Helper> helperIdToHelper = new HashMap<Long, Helper>();
        List<Helper> helpers = RepositoryProvider.getRepository(HelperRepository.class).findAll(sessionHolder.getToken());
        for (Helper helper : helpers)
        {
            helperIdToHelper.put(helper.getId(), helper);
        }
        // cache all positions
        HashMap<Integer, Position> posNumberToPos = new HashMap<Integer, Position>();
        List<Position> positions = RepositoryProvider.getRepository(PositionRepository.class).findAll(sessionHolder.getToken());
        for (Position position : positions)
        {
            posNumberToPos.put(position.getPositionNumber(), position);
        }
        Helper helper = null;
        Position position = null;
        for (Long helperId : helperIdToPosNumber.keySet())
        {
            helper = helperIdToHelper.get(helperId);
            position = posNumberToPos.get(helperIdToPosNumber.get(helperId));
            buildHelperAssignment(helper, position);
        }
    }

    private void buildHelperAssignment(Helper helper, Position position)
    {
        sessionHolder.saveOrUpdate(EntityFactory.buildHelperAssignment(helper, event, position, HelperAssignmentState.CONFIRMED));
    }

    private List<ImportRow> parseRows()
    {
        List<ImportRow> rows = new ArrayList<ImportRow>();
        JSONParser parser = new JSONParser();
        try
        {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Object obj = parser.parse(reader);

            JSONObject jsonObject = (JSONObject) obj;
            String name = (String) jsonObject.get("description");
            String author = (String) jsonObject.get("key");
            System.out.println(name);
            System.out.println(author);
            JSONArray companyList = (JSONArray) jsonObject.get("entries");
            int size = companyList.size();
            JSONObject moo = null;
            for (int index = 0; index < size; index++)
            {
                System.out.println("---------------------------------------------------");
                moo = (JSONObject) companyList.get(index);
                System.out.println("lastName : " + moo.get(JSON_PARAM_LAST_NAME));
                System.out.println("firstName : " + moo.get(JSON_PARAM_FIRST_NAME));
                System.out.println("mail : " + moo.get(JSON_PARAM_MAIL));
                System.out.println("domainNumber : " + moo.get(JSON_PARAM_DOMAIN_NUMBER));
                System.out.println("domainName : " + moo.get(JSON_PARAM_DOMAIN_NAME));
                System.out.println("positionNumber : " + moo.get(JSON_PARAM_POSITION_NUMBER));
                System.out.println("positionName : " + moo.get(JSON_PARAM_POSITION_NAME));
                rows.add(new ImportRow((String) moo.get(JSON_PARAM_LAST_NAME), (String) moo.get(JSON_PARAM_FIRST_NAME),
                        (String) moo.get(JSON_PARAM_BIRTHDAY), (String) moo.get(JSON_PARAM_MAIL),
                        (String) moo.get(JSON_PARAM_DOMAIN_NUMBER), (String) moo.get(JSON_PARAM_DOMAIN_NAME),
                        (String) moo.get(JSON_PARAM_POSITION_NUMBER), (String) moo.get(JSON_PARAM_POSITION_NAME),
                        (String) moo.get(JSON_PARAM_MINIMAL_AGE), (String) moo.get(JSON_PARAM_POSITION_PRIORITY)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rows;
    }

    // ---

    public static void main(String[] args)
    {
        new JsonEventImporter().doImport("Helfer_2015.json");
    }
}