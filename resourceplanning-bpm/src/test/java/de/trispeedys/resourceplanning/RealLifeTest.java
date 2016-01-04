package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.TestUtil;

public class RealLifeTest
{
    @Test
    public void testCreateHistoricEvents()
    {
        TestUtil.clearAll();

        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED, EventTemplate.TEMPLATE_TRI);
    }
}