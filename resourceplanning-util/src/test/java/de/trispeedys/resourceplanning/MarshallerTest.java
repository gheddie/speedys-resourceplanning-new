package de.trispeedys.resourceplanning;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.util.marshalling.ListMarshaller;
import static org.junit.Assert.assertEquals;

public class MarshallerTest
{
    @Test
    public void testBasicUnmarshalling()
    {
        assertEquals(3, ListMarshaller.unmarshall("12@34@56").size());
        assertEquals(1, ListMarshaller.unmarshall("12").size());
    }
    
    @Test
    public void testBasicMarshalling()
    {
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(new Integer(12));
        list1.add(new Integer(34));
        list1.add(new Integer(56));
        assertEquals("12@34@56", ListMarshaller.marshall(list1));
        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(new Integer(12));
        assertEquals("12", ListMarshaller.marshall(list2));
    }
}