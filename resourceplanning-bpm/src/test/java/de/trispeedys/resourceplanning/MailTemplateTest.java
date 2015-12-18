package de.trispeedys.resourceplanning;

import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.template.AlertDeactivationMailTemplate;
import de.trispeedys.resourceplanning.messaging.template.BookingConfirmationMailTemplate;
import de.trispeedys.resourceplanning.messaging.template.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.messaging.template.SendReminderMailTemplate;
import de.trispeedys.resourceplanning.service.MessagingService;

public class MailTemplateTest
{
    // TODO useful test ?!?
    //@Test
    public void testProposePositions()
    {
        // clear db
        HibernateUtil.clearAll();
        // create domains
        Domain domain1 = EntityFactory.buildDomain("dom1", 1).saveOrUpdate();
        Domain domain2 = EntityFactory.buildDomain("dom2", 2).saveOrUpdate();
        // create positions
        EntityFactory.buildPosition("Pos1", 12, domain1, 0, true).saveOrUpdate();
        EntityFactory.buildPosition("Pos2", 12, domain1, 1, true).saveOrUpdate();
        Position pos3 = EntityFactory.buildPosition("Pos3", 12, domain2, 2, true).saveOrUpdate();
        EntityFactory.buildPosition("Pos4", 12, domain2, 3, true).saveOrUpdate();
        EntityFactory.buildPosition("Pos5", 12, domain2, 4, true).saveOrUpdate();
        // create helper and event
        Helper helper =
                EntityFactory.buildHelper("H1_First", "H1_Last", "testhelper1.trispeedys@gmail.com ",
                        HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        
        EventTemplate evTemplate = EntityFactory.buildEventTemplate("123").saveOrUpdate();
        
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, evTemplate, null).saveOrUpdate();
        
        // send mail
        List<Position> positions = Datasources.getDatasource(Position.class).findAll(null);
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(helper, event, positions, HelperCallback.ASSIGNMENT_AS_BEFORE, pos3, false);
        MessagingService.createMessage("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com",
                template.constructSubject(), template.constructBody(), MessagingType.NONE, MessagingFormat.HTML, true);
        MessagingService.sendAllUnprocessedMessages();
    }
    
    // TODO useful test ?!?
    @Test   
    public void testBookingConfirmation()
    {
        System.out.println("---------------------------------------------------------------------------------------------");
        
        // clear db
        HibernateUtil.clearAll();        
        
        EventTemplate template = EntityFactory.buildEventTemplate("123ggg").saveOrUpdate();
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null).saveOrUpdate();
        
        Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1980).saveOrUpdate();
        
        Domain domain = EntityFactory.buildDomain("Schwimmen", 2).saveOrUpdate();
        Position pos = EntityFactory.buildPosition("Alles wegräumen", 12, domain, 0, true).saveOrUpdate();
        
        System.out.println(new BookingConfirmationMailTemplate(helper, event, pos).constructBody());
        
        System.out.println("---------------------------------------------------------------------------------------------");
    }
    
    // TODO useful test ?!?
    //@Test   
    public void testAlertDeactivation()
    {
        System.out.println("---------------------------------------------------------------------------------------------");
        
        // clear db
        HibernateUtil.clearAll();
        
        Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1980).saveOrUpdate();
        
        System.out.println(new AlertDeactivationMailTemplate(helper, null, null).constructBody());
        
        System.out.println("---------------------------------------------------------------------------------------------");
    }
    
    // TODO useful test ?!?
    //@Test   
    public void testSendReminderMail()
    {
        System.out.println("---------------------------------------------------------------------------------------------");
        
        // clear db
        HibernateUtil.clearAll();
        
        Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", "a1@b.de", HelperState.ACTIVE, 1, 2, 1980).saveOrUpdate();
        EventTemplate template = EntityFactory.buildEventTemplate("123ggg").saveOrUpdate();
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016, EventState.PLANNED, template, null).saveOrUpdate();
        Domain domain = EntityFactory.buildDomain("Schwimmen", 2).saveOrUpdate();
        Position pos = EntityFactory.buildPosition("Alles wegräumen", 12, domain, 0, true).saveOrUpdate();
        
        System.out.println(new SendReminderMailTemplate(helper, event, pos, true, 0).constructBody());
        
        System.out.println("---------------------------------------------------------------------------------------------");
    }
}