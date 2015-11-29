package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class EventTreeNode<T> extends EntityTreeNode<Event>
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
        return getPayLoad().toString();
    }
    
    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.EVENT;
    }
    
    public String itemKey()
    {
        Event eventItem = (Event) getPayLoad();
        return eventItem.getDifferentiator();
    }
    
    public String getAssignmentString()
    {
        return "";
    }
    
    public Long getEntityId()
    {
        return ((Event) getPayLoad()).getId();
    }
    
    public String getPriorization()
    {
        return "";
    }
    
    public String getAvailability()
    {
        return "";
    }
}