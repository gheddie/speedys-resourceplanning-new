package de.trispeedys.resourceplanning;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
    
    @Test
    public void testParseBoolean()
    {
        assertTrue(ParserUtil.parseBoolean("yes"));
        assertFalse(ParserUtil.parseBoolean("no"));
    }
}