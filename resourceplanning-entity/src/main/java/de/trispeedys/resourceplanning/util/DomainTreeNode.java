package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class DomainTreeNode<T> extends EntityTreeNode<Domain>
{
    public DomainTreeNode()
    {
        super();
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_DOMAIN;
    }

    public String infoString()
    {
        return getPayLoad().toString();
    }

    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.DOMAIN;
    }

    public String itemKey()
    {
        Domain eventItem = (Domain) getPayLoad();
        return eventItem.getDifferentiator();
    }

    public String getAssignmentString()
    {
        return "";
    }

    public Long getEntityId()
    {
        return ((Domain) getPayLoad()).getId();
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