package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "event_template", uniqueConstraints = @UniqueConstraint(columnNames =
{
        "description"
}))
public class EventTemplate extends AbstractDbObject
{
    public static final String ATTR_DESCRIPTION = "description";

    public static final String TEMPLATE_TRI = "TEMPLATE_TRI";
    
    public static final String TEMPLATE_CROSSZALES = "TEMPLATE_CROSSZALES";
    
    private String description;
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
}