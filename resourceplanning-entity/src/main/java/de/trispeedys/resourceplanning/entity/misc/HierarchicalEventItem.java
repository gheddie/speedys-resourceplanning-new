package de.trispeedys.resourceplanning.entity.misc;


public interface HierarchicalEventItem
{
    int LEVEL_EVENT = 0;
    
    int LEVEL_DOMAIN = 1;
    
    int LEVEL_POSITION = 2;    
    
    public int getHierarchyLevel();

    public String getDifferentiator();
}