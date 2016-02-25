package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.hibernate.Query;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.hibernateadapter.datasource.Datasources;
import de.trispeedys.resourceplanning.BusinessKeys;
import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.importer.JsonEventReader;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.PositionInclude;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

@WebService
@SOAPBinding(style = Style.RPC)
public class TestDataProvider
{
    public void startHelperRequestProcess(Long helperId, Long eventId)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helperId));
        variables.put(BpmVariables.Misc.VAR_EVENT_ID, new Long(eventId));
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId), variables);
    }

    public void duplicateUnchanged()
    {
        // real life event for 2015
        Event newEvent = RepositoryProvider.getRepository(EventRepository.class).findAll(null).get(0);
        SpeedyRoutines.duplicateEvent(newEvent, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null, null);
    }

    public void duplicate2015()
    {
        Domain domRadstrecke = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(2);
        EntityFactory.buildPosition("Helmkontrolle", 12, domRadstrecke, 777, true).saveOrUpdate();

        Domain domLaufstrecke = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(1);
        EntityFactory.buildPosition("Übergang Herrenfeldtstrasse", 12, domLaufstrecke, 888, true).saveOrUpdate();

        List<Integer> excludes = new ArrayList<Integer>();
        excludes.add(398);

        List<PositionInclude> includes = new ArrayList<PositionInclude>();
        includes.add(new PositionInclude(domRadstrecke, 777));
        includes.add(new PositionInclude(domLaufstrecke, 888));

        // real life event for 2015
        Event event2015 = RepositoryProvider.getRepository(EventRepository.class).findEventByEventKey("TRI-2015");
        SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, excludes, includes);
    }

    public void killAllExecutions()
    {
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        for (ProcessInstance instance : processEngine.getRuntimeService().createProcessInstanceQuery().list())
        {
            processEngine.getRuntimeService().deleteProcessInstance(instance.getId(), "moo");
        }
    }

    public void fireTimer(Long helperId, Long eventId)
    {
        if (helperId == null)
        {
            throw new ResourcePlanningException("fire timer --> no helper id provided!!");
        }
        if (eventId == null)
        {
            throw new ResourcePlanningException("fire timer --> no event id provided!!");
        }
        ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
        String businessKey = BusinessKeys.generateRequestHelpBusinessKey(helperId, eventId);
        List<ProcessInstance> executions = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceBusinessKey(businessKey).list();
        if ((executions == null) || (executions.size() != 1))
        {
            throw new ResourcePlanningException("none or more than one executions found for business key '" + businessKey + "'!!");
        }
        Execution execution = executions.get(0);
        List<Job> jobs = processEngine.getManagementService().createJobQuery().processInstanceId(execution.getId()).list();
        if ((jobs == null) || (jobs.size() == 0))
        {
            throw new ResourcePlanningException("no jobs found for execution with business key '" + businessKey + "'!!");
        }
        for (Job job : jobs)
        {
            processEngine.getManagementService().executeJob(job.getId());
        }
    }

    public int anonymizeHelperAddresses()
    {
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this, null);
        try
        {
            sessionHolder.beginTransaction();
            Query qry =
                    sessionHolder.createQuery("UPDATE " +
                            Helper.class.getSimpleName() + " SET " + Helper.ATTR_MAIL_ADDRESS + " = :address WHERE " + Helper.ATTR_MAIL_ADDRESS + " IS NOT NULL AND " + Helper.ATTR_MAIL_ADDRESS +
                            " <> ''");
            qry.setParameter("address", AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.PROCESS_TEST_MAIL));
            int rows = qry.executeUpdate();
            sessionHolder.commitTransaction();
            return rows;
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
            ;
            throw new ResourcePlanningException("helper addresses could not be anonymized : " + e.getMessage());
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }

    public void setupForTesting(String resourceName)
    {
        // (0) clear DB
        TestUtil.clearAll();

        System.out.println("setting up by resource '" + resourceName + "'...");

        // (1) kill all executions
        killAllExecutions();

        // (2) import json
        new JsonEventReader().doImport(resourceName);

        // (3) duplicate unchanged
        duplicateUnchanged();
    }
}