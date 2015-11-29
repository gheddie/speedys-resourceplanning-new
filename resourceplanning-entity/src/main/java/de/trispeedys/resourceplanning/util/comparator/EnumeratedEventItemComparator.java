package de.trispeedys.resourceplanning.util.comparator;

import java.util.Comparator;

import de.trispeedys.resourceplanning.entity.misc.EnumeratedEventItem;

public class EnumeratedEventItemComparator implements Comparator<EnumeratedEventItem>
{
    public int compare(EnumeratedEventItem e1, EnumeratedEventItem e2)
    {
        return getComparisonResult(e1, e2);
    }

    public static int getComparisonResult(EnumeratedEventItem e1, EnumeratedEventItem e2)
    {
        if (e1.getPagination() > e2.getPagination())
        {
            return 1;
        }
        if (e1.getPagination() < e2.getPagination())
        {
            return -1;
        }        
        return 0;
    }
}