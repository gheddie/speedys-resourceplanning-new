package de.trispeedys.resourceplanning.util;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityTreeNode<T>
{
    private Object payLoad;
    
    private List<Object> children;

    public EntityTreeNode()
    {
        super();
    }
    
    public void acceptChild(Object child)
    {
        if (children == null)
        {
            children = new ArrayList<Object>();
        }
        children.add(child);
    }

    public Object getPayLoad()
    {
        return payLoad;
    }
    
    public void setPayLoad(Object payLoad)
    {
        this.payLoad = payLoad;
    }
    
    public List<Object> getChildren()
    {
        return children;
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    public Object getHierarchicalItem()
    {
        return payLoad;
    }

    public abstract int getHierarchyLevel();

    public abstract String infoString();

    public abstract HierarchicalEventItemType getItemType();

    public abstract String itemKey();

    public abstract String getAssignmentString();
    
    public abstract Long getEntityId();

    public abstract String getPriorization();

    public abstract String getAvailability();
}