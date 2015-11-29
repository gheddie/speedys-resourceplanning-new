package de.trispeedys.resourceplanning.util.converter;

import java.util.HashMap;

import javax.xml.datatype.XMLGregorianCalendar;

public class ConverterUtil
{
    @SuppressWarnings("rawtypes")
    private static final HashMap<Class<?>, AbstractValueConverter> converters = new HashMap<Class<?>, AbstractValueConverter>();
    static
    {
        converters.put(XMLGregorianCalendar.class, new GregorianCalendarValueConverter());
    }

    public static <T> Object convert(Object object)
    {
        if (object == null)
        {
            return null;
        }
        Class<? extends Object> clazz = object.getClass();
        AbstractValueConverter<?> converter = converters.get(clazz);
        if (converter != null)
        {
            return converter.convert(object);
        }
        else
        {
            return object; 
        }
    }
}