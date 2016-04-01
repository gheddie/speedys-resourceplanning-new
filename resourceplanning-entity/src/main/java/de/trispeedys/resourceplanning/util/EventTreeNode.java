package de.trispeedys.resourceplanning.util;

import java.util.List;

import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class EventTreeNode<T> extends EntityTreeNode<GuidedEvent>
{
    public EventTreeNode()
    {
        super();
    }
    
    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_EVENT;
    }
    
    public String infoString()
    {
        return ((GuidedEvent) getPayLoad()).getDescription();
    }
    
    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.EVENT;
    }
    
    public String itemKey()
    {
        GuidedEvent eventItem = (GuidedEvent) getPayLoad();
        return eventItem.getDifferentiator();
    }
    
    public String getAssignmentString()
    {
        return "";
    }
    
    public Long getEntityId()
    {
        return ((GuidedEvent) getPayLoad()).getId();
    }
    
    public String getPriorization()
    {
        return "";
    }
    
    public String getAvailability(List<?> referencePositions, Object parent)
    {
        return "";
    }
}