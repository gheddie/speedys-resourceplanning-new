package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.misc.EnumeratedEventItem;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

@Entity
public class Position extends AbstractDbObject implements EnumeratedEventItem
{
    public static final String ATTR_POS_NUMBER = "positionNumber";
    
    // @Min(value = 2)
    private String description;
    
    @Column(name = "minimal_age")
    private int minimalAge;
    
    @NotNull
    @ManyToOne
    private Domain domain;
    
    @Column(name = "position_number")
    private int positionNumber;

    private boolean choosable;
    
    @Column(name = "assignment_priority")
    private Integer assignmentPriority;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getMinimalAge()
    {
        return minimalAge;
    }

    public void setMinimalAge(int minimalAge)
    {
        this.minimalAge = minimalAge;
    }
    
    public Domain getDomain()
    {
        return domain;
    }
    
    public void setDomain(Domain domain)
    {
        this.domain = domain;
    }
    
    public int getPositionNumber()
    {
        return positionNumber;
    }
    
    public void setPositionNumber(int positionNumber)
    {
        this.positionNumber = positionNumber;
    }
    
    public boolean isChoosable()
    {
        return choosable;
    }

    public void setChoosable(boolean choosable)
    {
        this.choosable = choosable;
    }
    
    public Integer getAssignmentPriority()
    {
        return assignmentPriority;
    }
    
    public void setAssignmentPriority(Integer assignmentPriority)
    {
        this.assignmentPriority = assignmentPriority;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+getPagination()+"] ["+description+", "+minimalAge+"]";
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_POSITION;
    }
    
    public String getDifferentiator()
    {
        return "[P"+getPagination()+"]";
    }
    
    public int getPagination()
    {
        return positionNumber;
    }
    
    public boolean equals(Object obj)
    {
        return (((Position) obj).getPositionNumber() == positionNumber);
    }
}