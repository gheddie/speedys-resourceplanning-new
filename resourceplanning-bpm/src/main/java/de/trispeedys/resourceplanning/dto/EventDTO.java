package de.trispeedys.resourceplanning.dto;

import java.util.Date;

public class EventDTO
{
    private String description;
    
    private Long eventId;

    private String eventState;
    
    private Date eventDate;
    
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;        
    }
    
    public String getEventState()
    {
        return eventState;
    }

    public void setEventState(String eventState)
    {
        this.eventState = eventState;        
    }
    
    public Date getEventDate()
    {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }
}