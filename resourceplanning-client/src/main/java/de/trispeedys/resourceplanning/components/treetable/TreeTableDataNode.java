package de.trispeedys.resourceplanning.components.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.StringUtil;

public class TreeTableDataNode
{
    private String description;

    private String assignment;
    
    private String group;
    
    private String priorization;
    
    private String availability;

    private List<TreeTableDataNode> children;

    private String itemType;

    private Long entityId;    

    public TreeTableDataNode(String description, String assignment, String group, String priorization, String availability, String itemType, Long entityId,
            List<TreeTableDataNode> children)
    {
        this.description = description;
        this.assignment = assignment;
        this.group = group;
        this.priorization = priorization;
        this.availability = availability;
        this.itemType = itemType;
        this.entityId = entityId;
        this.children = children;

        if (this.children == null)
        {
            this.children = Collections.emptyList();
        }
    }

    public String getDescription()
    {
        return description;
    }

    public String getAssignment()
    {
        return assignment;
    }
    
    public String getGroup()
    {
        return group;
    }
    
    public String getPriorization()
    {
        return priorization;
    }
    
    public String getAvailability()
    {
        return availability;
    }

    public List<TreeTableDataNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<TreeTableDataNode> children)
    {
        this.children = children;
    }

    public void addChild(TreeTableDataNode child)
    {
        if (children == null)
        {
            children = new ArrayList<TreeTableDataNode>();
        }
        children.add(child);
    }

    public Long getEntityId()
    {
        return entityId;
    }

    public HierarchicalEventItemType getEventItemType()
    {
        return (StringUtil.isBlank(itemType)
                ? null
                : HierarchicalEventItemType.valueOf(itemType));
    }

    /**
     * Knotentext vom JTree.
     */
    public String toString()
    {
        return description;
    }
}