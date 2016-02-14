package de.trispeedys.resourceplanning.util;

public class StringUtil
{
    public static boolean isBlank(String s)
    {
        return ((s == null) || (s.length() == 0));
    }
    
    public static String concatedObjects(Object... objects)
    {
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        for (Object o : objects)
        {
            buffer.append(o);
            if (index < objects.length - 1)
            {
                buffer.append("#");
            }
            index++;
        }
        return buffer.toString();
    }
}