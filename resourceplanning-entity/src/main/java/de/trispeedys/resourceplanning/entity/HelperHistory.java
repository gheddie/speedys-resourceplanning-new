package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.HistoryType;

@Entity
@Table(name = "helper_history")
public class HelperHistory extends AbstractDbObject
{
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "history_type")
    @NotNull
    private HistoryType historyType;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    public Date getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }

    public HistoryType getHistoryType()
    {
        return historyType;
    }

    public void setHistoryType(HistoryType historyType)
    {
        this.historyType = historyType;
    }

    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }
}