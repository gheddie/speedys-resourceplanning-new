package de.trispeedys.resourceplanning;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.XmlReader;

public class ParseEventTest
{
    @Test
    public void testParseTriathlon2015()
    {
        HibernateUtil.clearAll();
        
        // create event template
        EntityFactory.buildEventTemplate("TRI").saveOrUpdate();
        
        // create required domains
        Domain dom1 = EntityFactory.buildDomain("Startunterlagen Samstag", 1).saveOrUpdate();
        Domain dom2 = EntityFactory.buildDomain("Startunterlagen Sonntag", 2).saveOrUpdate();        
        
        // create required positions
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 1", 12, dom1, 1, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 2", 12, dom1, 2, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 3", 12, dom1, 3, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 4", 12, dom1, 4, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 5", 12, dom1, 5, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 6", 12, dom1, 6, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 7", 12, dom1, 7, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 8", 12, dom2, 8, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 9", 12, dom2, 9, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 10", 12, dom2, 10, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 11", 12, dom2, 11, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 12", 12, dom2, 12, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 13", 12, dom2, 13, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 14", 12, dom2, 14, true).saveOrUpdate();
        EntityFactory.buildPosition("Aufgabeneinweisung vor Ort 15", 12, dom2, 15, true).saveOrUpdate();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 1", 12, dom2, 16, true).saveOrUpdate();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 2", 12, dom2, 17, true).saveOrUpdate();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 3", 12, dom2, 18, true).saveOrUpdate();
        EntityFactory.buildPosition("Hand- bzw. Wadenbeschriftung 4", 12, dom2, 19, true).saveOrUpdate();
        
        // create helpers
        EntityFactory.buildHelper("Abend", "Judith", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Scharke", "Simone", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Melsner", "Conny", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Krohne", "Kiki", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Wackerhage", "Tanja", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Homann", "Carolin", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Kessler", "Elke", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Mabend", "Judith", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Fornefett", "Iris ", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Elsner", "Conny", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Mohne", "Kiki", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("André", "Ingrid", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("André", "Jakob", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Momann", "Carolin", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Messler", "Elke", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Obi-Popadic", "Ifeoma ", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Popadic", "Niko", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Scharke", "Thomas", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("Amrhein-Thomala", "May-Britt", "a@b.de", HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        
        try
        {
            Document doc = XmlReader.readXml(getClass().getClassLoader().getResourceAsStream("event_tri_2015.xml"));
            SpeedyRoutines.parseEvent(doc);
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}