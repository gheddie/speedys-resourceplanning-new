package de.trispeedys.resourceplanning.util;

public class StringUtil
{
    public static boolean isBlank(String s)
    {
        return ((s == null) || (s.length() == 0));
    }
}