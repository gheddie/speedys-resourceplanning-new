package de.trispeedys.resourceplanning.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import de.trispeedys.resourceplanning.entity.misc.EnumeratedEventItem;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames =
{
        "name"
}))
public class Domain extends AbstractDbObject implements EnumeratedEventItem
{
    public static final String ATTR_DOMAIN_NUMBER = "domainNumber";
    
    private String name;
    
    @Column(name = "domain_number")
    private int domainNumber;

    @OneToMany
    @JoinColumn(name = "domain_id")
    private List<Position> positions;
    
    public int getDomainNumber()
    {
        return domainNumber;
    }
    
    public void setDomainNumber(int domainNumber)
    {
        this.domainNumber = domainNumber;
    }
 
    /*
    public Helper getLeader()
    {
        return leader;
    }
    */
    
    /*
    public void setLeader(Helper leader)
    {
        this.leader = leader;
    }
    */
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public List<Position> getPositions()
    {
        return positions;
    }
    
    public void setPositions(List<Position> positions)
    {
        this.positions = positions;
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_DOMAIN;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+name+", "+domainNumber+"]";
    }

    public String getDifferentiator()
    {
        return "[D"+getPagination()+"]";
    }

    public int getPagination()
    {
        return domainNumber;
    }
}