package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "missed_assignment")
public class MissedAssignment extends AbstractAssignment
{
    public static final String ATTR_USED = "used";
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_stamp")
    private Date timeStamp;
    
    private boolean used;
    
    public Date getTimeStamp()
    {
        return timeStamp;
    }
    
    public void setTimeStamp(Date timeStamp)
    {
        this.timeStamp = timeStamp;
    }
    
    public boolean isUsed()
    {
        return used;
    }

    public void setUsed(boolean used)
    {
        this.used = used;
    }
}