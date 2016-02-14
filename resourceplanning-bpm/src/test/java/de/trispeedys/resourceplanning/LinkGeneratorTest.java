package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import de.trispeedys.resourceplanning.util.LinkGenerator;

public class LinkGeneratorTest
{
    private static final String TEMPLATE = "http://localhost:8080/resourceplanning-bpm-1.0-RC-4/AssignmentCancellationReceiver.jsp?helperId=4236&eventId=4258&positionId=4243";

    // @Test
    // How to test this --> hash map has no fixed order...
    public void testGeneration()
    {
        String baseLink = "http://localhost:8080/resourceplanning-bpm-1.0-RC-4";
        String receiverPageName = "AssignmentCancellationReceiver.jsp";
        
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("helperId", new Long(4326));
        parameters.put("eventId", new Long(4258));
        parameters.put("positionId", new Long(4243));
        
        assertEquals(TEMPLATE, new LinkGenerator(baseLink, receiverPageName, parameters).generate());
    }
}