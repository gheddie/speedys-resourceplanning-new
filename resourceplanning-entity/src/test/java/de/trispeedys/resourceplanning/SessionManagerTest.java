package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.interceptor.GenericEntityInceptor;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.TestUtil;

public class SessionManagerTest
{
    @Test
    public void testInterceptor()
    {
        // clear db
        TestUtil.clearAll();
        
        SessionHolder holder = SessionManager.getInstance().registerSession(this, new GenericEntityInceptor());
        SessionToken token = holder.getToken();
        try
        {            
            holder.beginTransaction();
            
            EventTemplate template = EntityFactory.buildEventTemplate("123ggg");
            holder.saveOrUpdate(template);

            // build event
            GuidedEvent event = EntityFactory.buildEvent("TRI-PETER", "TRI-PETER", 21, 12, 2014, EventState.FINISHED, template, null);
            holder.saveOrUpdate(event);
            
            // create helpers
            Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", "a@b.de", HelperState.ACTIVE, 1, 2, 1980, true);
            holder.saveOrUpdate(helper);
            
            // build domain
            Domain domain = EntityFactory.buildDomain("D1", 1);
            holder.saveOrUpdate(domain);
            
            // build positions
            Position pos1 = EntityFactory.buildPosition("P1", 12, domain, 0, true);
            holder.saveOrUpdate(pos1);
            Position pos2 = EntityFactory.buildPosition("P2", 13, domain, 1, true);
            holder.saveOrUpdate(pos2);
            
            // relate positions
            SpeedyRoutines.relatePositionsToEvent(token, event, pos1, pos2);
            
            HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
            
            // assign helpers
            repository.assignHelper(helper, event, pos1, token);
            
            holder.commitTransaction();   
        }
        catch (Exception e)
        {
            holder.rollbackTransaction();
            throw new ResourcePlanningException("error on testInterceptor()", e);
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(holder);            
        }        
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testTransactionRollback()
    {
        // clear db
        TestUtil.clearAll();
        
        // assertEquals(0, SessionManager.getInstance().getOpenSessionCount());
        
        SessionHolder holder = SessionManager.getInstance().registerSession(this, null);
        
        // assertEquals(1, SessionManager.getInstance().getOpenSessionCount());
        
        try
        {
            holder.beginTransaction();
            
            // create some stuff
            holder.saveOrUpdate(EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1980, true));
            holder.saveOrUpdate(EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1981, true));
            holder.saveOrUpdate(EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1982, true));
            holder.saveOrUpdate(EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1983, true));
            holder.saveOrUpdate(EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1984, true));
            if (true)
            {
                throw new ResourcePlanningException("123");   
            }            
            holder.commitTransaction();   
        }
        catch (Exception e)
        {
            holder.rollbackTransaction();
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(holder);            
            // assertEquals(0, SessionManager.getInstance().getOpenSessionCount());
        }
        // created stuff must be gone...
        assertEquals(0, RepositoryProvider.getRepository(HelperRepository.class).findAll(null).size());
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testExtended()
    {
        // clear db
        TestUtil.clearAll();
        
        HelperRepository repository = RepositoryProvider.getRepository(HelperRepository.class);
        
        // create some helpers and then delete one of them (single use session/transaction)
        Helper helper1 = EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 13, 2, 1976, true).saveOrUpdate();
        Helper helper2 = EntityFactory.buildHelper("Schulz", "Diana", "a@b.de", HelperState.ACTIVE, 4, 3, 1973, true).saveOrUpdate();
        
        // there must be 2 helpers...
        assertEquals(2,  repository.findAll(null).size());
        
        repository.remove(helper1);
        
        // there must be one helper left and do open transaction
        // assertEquals(0,  SessionManager.getInstance().getOpenSessionCount());
        assertEquals(1,  repository.findAll(null).size());
        
        // clear db
        TestUtil.clearAll();
        
        // now with a managed transaction...
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this, null);
        try
        {
            sessionHolder.beginTransaction();
            
            Helper helper3 = EntityFactory.buildHelper("Schulz", "Tim", "a@b.de", HelperState.ACTIVE, 11, 11, 2005, true);
            Helper helper4 = EntityFactory.buildHelper("Schulz", "Fabian", "a@b.de", HelperState.ACTIVE, 21, 5, 2011, true);
            
            sessionHolder.saveOrUpdate(helper3);
            sessionHolder.saveOrUpdate(helper4);
            
            sessionHolder.remove(helper3);
            
            if (true)
            {
                throw new ResourcePlanningException("123"); 
            }
            
            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
        
        // no helpers as tx was rolled back...
        assertEquals(0, repository.findAll(null).size());
    }
}