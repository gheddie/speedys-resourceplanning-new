package de.trispeedys.resourceplanning;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.gravitex.hibernateadapter.core.SessionHolder;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.TestUtil;

public class CrosszalesTest
{
    @Test
    public void testSetupCrosszales()
    {
        // (0) clear DB
        TestUtil.clearAll();
        
        int posNumber = 1;
        
        SessionHolder sessionHolder = SessionManager.getInstance().registerSession(this, null);
        try
        {
            sessionHolder.beginTransaction();
            
            EventTemplate template = EntityFactory.buildEventTemplate("CROSSZALES");
            sessionHolder.saveOrUpdate(template);
            
            GuidedEvent event = EntityFactory.buildEvent("Crosszales 2015", "CROSS-2015", 1, 9, 2015, EventState.FINISHED, template, null);
            
            sessionHolder.saveOrUpdate(event);

            // before event
            
            Domain domainBefore = EntityFactory.buildDomain("Vor dem Event", 200);
            sessionHolder.saveOrUpdate(domainBefore);
            
            List<AddPosition> beforeEvent = new ArrayList<>();
            
            beforeEvent.add(new AddPosition("Oma Beach, Single Trail, Double Wall 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Oma Beach, Single Trail, Double Wall 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Oma Beach, Single Trail, Double Wall 3", 12, "", ""));
            beforeEvent.add(new AddPosition("Oma Beach, Single Trail, Double Wall 4", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Farmers End", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Farmers End, Wettkampfbesprechung", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Grillstand 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Grillstand 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Grillstand Leitung", 12, "", ""));
            beforeEvent.add(new AddPosition("Schranke Rot/Weiﬂ, Rabbit trac / Hot Wheels 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Schranke Rot/Weiﬂ, Rabbit trac / Hot Wheels 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Schranke Rot/Weiﬂ, Rabbit trac / Hot Wheels 3", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau M-Hill und Stangen nach Parkplatz runter zum See 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau M-Hill und Stangen nach Parkplatz runter zum See 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Zielbereich, Beschallung, Diver und Quicksand 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Zielbereich, Beschallung, Diver und Quicksand 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Zielbereich, Beschallung, Diver und Quicksand 3", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Zielbereich, Beschallung, Diver und Quicksand 4", 12, "", ""));
            beforeEvent.add(new AddPosition("Aufbau Zielbereich, Beschallung, Diver und Quicksand 5", 12, "", ""));
            beforeEvent.add(new AddPosition("Startunterlagenausgabe 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Startunterlagenausgabe 2", 12, "", ""));
            beforeEvent.add(new AddPosition("Startunterlagenausgabe 3", 12, "", ""));
            beforeEvent.add(new AddPosition("Startunterlagenausgabe 4", 12, "", ""));
            beforeEvent.add(new AddPosition("Zeitnahme 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Ziel/Rundenverpflegung 1", 12, "", ""));
            beforeEvent.add(new AddPosition("Ziel/Rundenverpflegung 2", 12, "", ""));
            
            // add positions to 'before event'
            Position posBefore = null;
            for (AddPosition addPositionBefore : beforeEvent)
            {
                // create position...
                posBefore = EntityFactory.buildPosition(addPositionBefore.getDescription(), addPositionBefore.getMinimalAge(), domainBefore, posNumber++, true);
                sessionHolder.saveOrUpdate(posBefore);
                
                // ...and event it to the event
                sessionHolder.saveOrUpdate(EntityFactory.buildEventPosition(event, posBefore));
            }
            
            // while event
            
            Domain domainWhile = EntityFactory.buildDomain("Nach dem Event", 201);
            sessionHolder.saveOrUpdate(domainWhile);
            
            List<AddPosition> whileEvent = new ArrayList<>();
            
            whileEvent.add(new AddPosition("Abbiegung nach 100m", 12, "", ""));
            whileEvent.add(new AddPosition("Quick Sand", 12, "", ""));
            whileEvent.add(new AddPosition("Double Wall, bei 8km auch runter zum See", 12, "", ""));
            whileEvent.add(new AddPosition("Einweiser nach Double Wall runter", 12, "", ""));
            whileEvent.add(new AddPosition("Start/Ziel", 12, "", ""));
            whileEvent.add(new AddPosition("Assistent Siegerehrung", 12, "", ""));
            whileEvent.add(new AddPosition("Single Trail", 12, "", ""));
            whileEvent.add(new AddPosition("Grillstand 1", 12, "", ""));
            whileEvent.add(new AddPosition("Grillstand 2", 12, "", ""));
            whileEvent.add(new AddPosition("Hot Wheels", 12, "", ""));
            whileEvent.add(new AddPosition("Rabbit Trac", 12, "", ""));
            whileEvent.add(new AddPosition("Schranke rot-weiﬂ", 12, "", ""));
            whileEvent.add(new AddPosition("M-Hill", 12, "", ""));
            whileEvent.add(new AddPosition("P runter zum See", 12, "", ""));
            whileEvent.add(new AddPosition("Oma Beach", 12, "", ""));
            whileEvent.add(new AddPosition("Farmers End", 12, "", ""));
            whileEvent.add(new AddPosition("MTB vor erstem L‰ufer", 12, "", ""));
            whileEvent.add(new AddPosition("Sprecher/Siegerehrung", 12, "", ""));
            whileEvent.add(new AddPosition("Diver", 12, "", ""));
            whileEvent.add(new AddPosition("Zeitnahme 2", 12, "", ""));
            whileEvent.add(new AddPosition("Rundenkontrolle", 12, "", ""));
            whileEvent.add(new AddPosition("Zeitnahme 3", 12, "", ""));
            whileEvent.add(new AddPosition("Zielverpflegung 1", 12, "", ""));
            whileEvent.add(new AddPosition("Zielverpflegung 2", 12, "", ""));
            
            // add positions to 'while event'
            Position posWhile = null;
            for (AddPosition addPositionWhile : whileEvent)
            {
                // create position...
                posWhile = EntityFactory.buildPosition(addPositionWhile.getDescription(), addPositionWhile.getMinimalAge(), domainWhile, posNumber++, true);
                sessionHolder.saveOrUpdate(posWhile);
                
                // ...and event it to the event
                sessionHolder.saveOrUpdate(EntityFactory.buildEventPosition(event, posWhile));
            }
            
            sessionHolder.commitTransaction();
        }
        catch (Exception e)
        {
            sessionHolder.rollbackTransaction();
            throw e;
        }
        finally
        {
            SessionManager.getInstance().unregisterSession(sessionHolder);
        }
    }
}