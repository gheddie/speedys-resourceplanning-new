package de.trispeedys.resourceplanning.dto;

public class HierarchicalEventItemDTO
{
    private String itemType;
    
    private String infoString;

    private int hierarchyLevel;

    private String itemKey;
    
    private String assignmentString;

    private Long entityId;

    private String priorization;
    
    private String availability;

    private String group;
    
    public String getItemType()
    {
        return itemType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }
    
    public String getInfoString()
    {
        return infoString;
    }

    public void setInfoString(String infoString)
    {
        this.infoString = infoString;
    }
    
    public int getHierarchyLevel()
    {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(int hierarchyLevel)
    {
        this.hierarchyLevel = hierarchyLevel;
    }
    
    public String getItemKey()
    {
        return itemKey;
    }

    public void setItemKey(String itemKey)
    {
        this.itemKey = itemKey;
    }
    
    public String getAssignmentString()
    {
        return assignmentString;
    }
    
    public void setAssignmentString(String assignmentString)
    {
        this.assignmentString = assignmentString;
    }
    
    public Long getEntityId()
    {
        return entityId;
    }

    public void setEntityId(Long entityId)
    {
        this.entityId = entityId;
    }
    
    public String getPriorization()
    {
        return priorization;
    }

    public void setPriorization(String priorization)
    {
        this.priorization = priorization;        
    }
    
    public String getAvailability()
    {
        return availability;
    }
    
    public void setAvailability(String availability)
    {
        this.availability = availability;
    }
    
    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }
}