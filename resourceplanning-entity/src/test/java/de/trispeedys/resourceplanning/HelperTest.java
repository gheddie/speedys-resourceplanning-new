package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class HelperTest
{
    @Test
    public void testFirstAssignment()
    {
        HibernateUtil.clearAll();
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", HelperAssignmentTest.TEST_MAIL_ADDRESS,
                        HelperState.ACTIVE, 13, 2, 1976).saveOrUpdate();
        assertEquals(true, AssignmentService.isFirstAssignment(helper.getId()));
    }

    /**
     * A helper can have two assignments in one event!!
     */
    // @Test
    public void testDuplicateAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        // create helper
        Helper helper =
                EntityFactory.buildHelper("Diana", "Schulz", "a@b.de", HelperState.ACTIVE, 4, 3, 1973)
                        .saveOrUpdate();
        // create domain
        Domain domain = EntityFactory.buildDomain("someDomain", 1).saveOrUpdate();
        // create positions
        Position pos1 = EntityFactory.buildPosition("Nudelparty", 12, domain, 0, true).saveOrUpdate();
        Position pos2 = EntityFactory.buildPosition("Laufstrecke", 12, domain, 1, true).saveOrUpdate();

        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // create event
        Event tri2014 =
                EntityFactory.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014, EventState.PLANNED,
                        template, null).saveOrUpdate();
        // assign positions to that event
        SpeedyRoutines.relatePositionsToEvent(tri2014, pos1, pos2);
        // assign helper to both positions
        SpeedyRoutines.assignHelperToPositions(helper, tri2014, pos1, pos2);
    }

    @Test
    public void testSelectActiveHelperIds()
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createSimpleEvent("TRI", "TRI", 1, 1, 1980, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        assertEquals(5, HelperService.queryActiveHelperIds().size());
    }

    @Test
    public void testCreateHelperId()
    {
        assertEquals("PADE29051964", SpeedyRoutines.createHelperCode(EntityFactory.buildHelper("Päge", "Denny", "a@b.de",
                HelperState.ACTIVE, 29, 5, 1964)));
        assertEquals("KLWI13111964", SpeedyRoutines.createHelperCode(EntityFactory.buildHelper("Klopp", "Willi", "a@b.de",
                HelperState.ACTIVE, 13, 11, 1964)));
        assertEquals("BELA04041971", SpeedyRoutines.createHelperCode(EntityFactory.buildHelper("Beyer", "Lars", "a@b.de",
                HelperState.ACTIVE, 4, 4, 1971)));
        assertEquals("LULO04041971", SpeedyRoutines.createHelperCode(EntityFactory.buildHelper("Lüge", "Lothar", "a@b.de",
                HelperState.ACTIVE, 4, 4, 1971)));
        assertEquals("LOLE04041971", SpeedyRoutines.createHelperCode(EntityFactory.buildHelper("Löge", "Lennart", "a@b.de",
                HelperState.ACTIVE, 4, 4, 1971)));
    }
    
    @Test
    public void testHelperFitsPosition()
    {
        HibernateUtil.clearAll();
        
        // helper turns 16 on 15.06.2016
        EntityFactory.buildHelper("Schulz", "Stefan", "a@b.de", HelperState.ACTIVE, 15, 6, 2000).saveOrUpdate();
        
        Domain domain = EntityFactory.buildDomain("D1", 787).saveOrUpdate();
        Position pos = EntityFactory.buildPosition("P1", 16, domain, 53, true).saveOrUpdate();
        
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findAll().get(0);
        
        assertFalse(helper.isAssignableTo(pos, makeDate(14, 6, 2014)));
        assertFalse(helper.isAssignableTo(pos, makeDate(14, 6, 2016)));
        assertTrue(helper.isAssignableTo(pos, makeDate(15, 6, 2016)));
        assertTrue(helper.isAssignableTo(pos, makeDate(15, 6, 2018)));
    }
    
    @Test
    public void testCreateUserTestEvent()
    {
        HibernateUtil.clearAll();
        
        TestDataGenerator.createUserTestEvent("Test-Duathlon 2015", "DU-TEST-2015", 1, 3, 2015, EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
    }

    private Date makeDate(int day, int month, int year)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, (month-1));
        cal.set(Calendar.YEAR, year);
        Date result = cal.getTime();
        return result;
    }
}