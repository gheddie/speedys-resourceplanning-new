package de.trispeedys.resourceplanning.importer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class JsonEventImporter
{

    private List<ImportRow> rows;

    private Event event;

    private HashMap<Long, Integer> helperIdToPosNumber = new HashMap<Long, Integer>();

    private String resourceName;

    private void doImport(String aResourceName)
    {
        this.resourceName = aResourceName;
        HibernateUtil.clearAll();
        EventTemplate eventTemplate = EntityFactory.buildEventTemplate("TRI").saveOrUpdate();
        event = EntityFactory.buildEvent("Triathlon 2015", "TRI-2015", 1, 1, 2015, EventState.FINISHED, eventTemplate, null).saveOrUpdate();
        rows = parseRows();
        createHelpers();
        createPositions();
        createAssignments();
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
            domainsToPositions.get(row.getDomainNumber()).add(EntityFactory.buildPosition(row.getPosition(), 12, null, row.getPositionNumber(), true));
        }
        Domain persistedDomain = null;
        for (Integer domainNumber : domainsToPositions.keySet())
        {
            persistedDomain = EntityFactory.buildDomain(domainNumberToDomainName.get(domainNumber), domainNumber);
            persistedDomain.saveOrUpdate();
            for (Position pos : domainsToPositions.get(domainNumber))
            {
                pos.setDomain(persistedDomain);
                pos.saveOrUpdate();
                // relate position to event
                EntityFactory.buildEventPosition(event, pos).saveOrUpdate();
            }
        }
    }

    private void createHelpers()
    {
        Helper buildedHelper = null;
        for (ImportRow row : rows)
        {
            try
            {
                buildedHelper =
                        EntityFactory.buildHelper(row.getHelperLastName(), row.getHelperFirstName(), row.getHelperMail(), HelperState.ACTIVE, row.getDateOfBirth());
                buildedHelper.saveOrUpdate();
                helperIdToPosNumber.put(buildedHelper.getId(), new Integer(row.getPositionNumber()));
            }
            catch (Exception e)
            {
                System.out.println(" ### ERROR ### : " + e.getMessage());
            }

        }
    }

    private void createAssignments()
    {
        // cache all helpers
        HashMap<Long, Helper> helperIdToHelper = new HashMap<Long, Helper>();
        for (Helper helper : RepositoryProvider.getRepository(HelperRepository.class).findAll())
        {
            helperIdToHelper.put(helper.getId(), helper);
        }
        // cache all positions
        HashMap<Integer, Position> posNumberToPos = new HashMap<Integer, Position>();
        for (Position position : RepositoryProvider.getRepository(PositionRepository.class).findAll())
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
        EntityFactory.buildHelperAssignment(helper, event, position, HelperAssignmentState.CONFIRMED).saveOrUpdate();
    }

    private List<ImportRow> parseRows()
    {
        List<ImportRow> rows = new ArrayList<ImportRow>();
        JSONParser parser = new JSONParser();
        try
        {
            BufferedInputStream inputStream = (BufferedInputStream) getClass().getClassLoader().getResourceAsStream(resourceName);
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
                System.out.println("lastName : " + moo.get("ln"));
                System.out.println("firstName : " + moo.get("fn"));
                System.out.println("mail : " + moo.get("ml"));
                System.out.println("domainNumber : " + moo.get("dr"));
                System.out.println("domainName : " + moo.get("dn"));
                System.out.println("positionNumber : " + moo.get("pr"));
                System.out.println("positionName : " + moo.get("pn"));
                rows.add(new ImportRow((String) moo.get("ln"), (String) moo.get("fn"), (String) moo.get("db"), (String) moo.get("ml"), (String) moo.get("dr"),
                        (String) moo.get("dn"), (String) moo.get("pr"), (String) moo.get("pn")));
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
        new JsonEventImporter().doImport("Helfer_2015_2.json");
    }
}