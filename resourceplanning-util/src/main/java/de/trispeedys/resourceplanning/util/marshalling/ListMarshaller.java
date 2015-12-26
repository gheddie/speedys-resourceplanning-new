package de.trispeedys.resourceplanning.util.marshalling;

import java.util.ArrayList;
import java.util.List;

import de.trispeedys.resourceplanning.util.StringUtil;

public class ListMarshaller
{
    public static final String SEPERATOR = "@";
    
    public static List<Integer> unmarshall(String s)
    {
        if (StringUtil.isBlank(s))
        {
            return null;
        }
        List<Integer> result = new ArrayList<Integer>();
        for (String t : s.split(SEPERATOR))
        {
            result.add(Integer.parseInt(t));
        }
        return result;
    }

    public static String marshall(List<Integer> list)
    {
        String result = "";
        int index = 0;
        for (Integer i : list)
        {
            result += i;
            if (index < list.size() - 1)
            {
                result += SEPERATOR;
            }
            index++;
        }
        return result;
    }
}