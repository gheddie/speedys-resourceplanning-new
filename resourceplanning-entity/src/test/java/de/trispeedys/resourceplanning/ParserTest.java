package de.trispeedys.resourceplanning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import de.trispeedys.resourceplanning.util.parser.ParserUtil;

public class ParserTest
{
    @Test
    public void testParseLong()
    {
        assertEquals(new Long(1261), ParserUtil.parseLong(new Long(1261).toString()));
        assertEquals(new Long(1261), ParserUtil.parseLong("1.261"));
        assertEquals(new Long(1261), ParserUtil.parseLong("1,261"));
    }
}