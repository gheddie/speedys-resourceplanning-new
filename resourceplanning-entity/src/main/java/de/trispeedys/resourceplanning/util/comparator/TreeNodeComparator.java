package de.trispeedys.resourceplanning.util.comparator;

import java.util.Comparator;

import de.trispeedys.resourceplanning.entity.misc.EnumeratedEventItem;
import de.trispeedys.resourceplanning.util.EntityTreeNode;

public class TreeNodeComparator implements Comparator<EntityTreeNode>
{
    public int compare(EntityTreeNode node1, EntityTreeNode node2)
    {
        EnumeratedEventItem item1 = (EnumeratedEventItem) node1.getPayLoad();
        EnumeratedEventItem item2 = (EnumeratedEventItem) node2.getPayLoad();
        
        return EnumeratedEventItemComparator.getComparisonResult(item1, item2);
    }
}