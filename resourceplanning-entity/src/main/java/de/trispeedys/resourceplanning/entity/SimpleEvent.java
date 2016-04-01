package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

@Entity(name = "simple_event")
@Inheritance(strategy=InheritanceType.JOINED)
public class SimpleEvent extends AbstractDbObject
{
    @Column(name = "event_key")
    @NotNull
    @Length(min = 5)
    private String eventKey;
    
    @NotNull
    @Length(min = 5)
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "event_date")
    private Date eventDate;
    
    private boolean enrollable;
    
    public String getEventKey()
    {
        return eventKey;
    }

    public void setEventKey(String eventKey)
    {
        this.eventKey = eventKey;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+description+", "+eventKey+"]";
    }
    
    public Date getEventDate()
    {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }
    
    public boolean isEnrollable()
    {
        return this.enrollable;
    }
    
    public void setEnrollable(boolean enrollable)
    {
        this.enrollable = enrollable;
    }
}