package de.trispeedys.resourceplanning.util.comparator;

import java.util.Comparator;

import de.trispeedys.resourceplanning.entity.MissedAssignment;

public class MissedAssignmentComparator implements Comparator<MissedAssignment>
{
    public int compare(MissedAssignment o1, MissedAssignment o2)
    {
        // TODO make sure ordering is corrected --> youngest first !!!
        return o1.getTimeStamp().compareTo(o2.getTimeStamp());
    }
}