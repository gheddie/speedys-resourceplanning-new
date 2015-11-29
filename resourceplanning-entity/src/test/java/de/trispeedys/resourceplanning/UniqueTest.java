package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

/**
 * Tests the unique criterias for the {@link HelperAssignment} entity.
 * 
 * @author Stefan.Schulz
 *
 */
public class UniqueTest
{
    /**
     * It must be possible to have more than one {@link HelperAssignment} with the same event and position, as long
     * as only one of them is {@link HelperAssignmentState#CONFIRMED}.
     */
    @Test
    public void testReassignCancelledAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        
        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();
        
        // create domain
        Domain domain1 = EntityFactory.buildDomain("dom1", 1).saveOrUpdate();

        Position position = EntityFactory.buildPosition("Some position", 12, domain1, 0, true).saveOrUpdate();
        Event event = EntityFactory.buildEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016, EventState.PLANNED, template, null).saveOrUpdate();
        Helper helper1 =
                EntityFactory.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).saveOrUpdate();
        Helper helper2 =
                EntityFactory.buildHelper("Diana", "Schulz", "a@b.de", HelperState.ACTIVE, 4, 3, 1973).saveOrUpdate();
        
        //assign position to event to avoid resource planning exception
        SpeedyRoutines.relatePositionsToEvent(event, position);
        
        EntityFactory.buildHelperAssignment(helper1, event, position, HelperAssignmentState.CONFIRMED).saveOrUpdate();
        EntityFactory.buildHelperAssignment(helper2, event, position, HelperAssignmentState.CANCELLED).saveOrUpdate();
    }
    
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void testPositionUniqueByEnumeration()
    {
        // clear db
        HibernateUtil.clearAll();
        
        Domain dom = EntityFactory.buildDomain("123", 123).saveOrUpdate();
        EntityFactory.buildPosition("Ansage Zieleinlauf", 12, dom , 173, true).saveOrUpdate();
        EntityFactory.buildPosition("Ansage Zieleinlauf", 12, dom, 173, true).saveOrUpdate();
    }
    
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void testDomainUniqueByEnumeration()
    {
        // clear db
        HibernateUtil.clearAll();
        
        EntityFactory.buildDomain("D1", 173).saveOrUpdate();
        EntityFactory.buildDomain("D1", 173).saveOrUpdate();
    }    
}