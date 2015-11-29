package de.trispeedys.resourceplanning.util;

public class StringUtil
{
    public static boolean isBlank(String s)
    {
        return ((s == null) || (s.length() == 0));
    }

    public static String setterName(String attribute)
    {
        return "set" + firstToUpper(attribute);
    }

    public static String firstToUpper(String s)
    {
        String result = "";
        result += s.charAt(0);
        result = result.toUpperCase();
        return result += s.substring(1, s.length());
    }
}