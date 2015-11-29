package de.trispeedys.resourceplanning.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

public class DateHelper
{

    public static DateTime toDateTime(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new DateTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));
    }
}