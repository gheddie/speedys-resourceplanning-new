package de.trispeedys.resourceplanning.util.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.trispeedys.resourceplanning.util.StringUtil;

public class ParserUtil
{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    
    public static boolean parseBoolean(String value)
    {
        if (StringUtil.isBlank(value))
        {
            return false;
        }
        return (value.equals("y"));
    }
    
    public static Long parseLong(String value)
    {
        if (StringUtil.isBlank(value))
        {
            return null;
        }
        value = value.replaceAll("\\.", "");
        value = value.replaceAll("\\,", "");
        return Long.parseLong(value);
    }

    public static Date parseDate(String value) throws ParseException
    {
        return DATE_FORMAT.parse(value);
    }
}