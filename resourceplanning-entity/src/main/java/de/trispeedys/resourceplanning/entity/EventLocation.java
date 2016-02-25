package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

@Entity
@Table(name = "event_location")
public class EventLocation extends AbstractDbObject
{
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