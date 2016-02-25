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

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

@Entity
@Table(name = "message_queue")
public class MessageQueue extends AbstractDbObject
{
    public static final String ATTR_FROM_ADDRESS = "fromAddress";
    
    public static final String ATTR_TO_ADDRESS = "toAddress";
    
    public static final String ATTR_SUBJECT = "subject";
    
    public static final String ATTR_BODY = "body";
    
    public static final String ATTR_MESSAGING_TYPE = "messagingType";

    public static final String ATTR_HELPER = "helper";
    
    @Column(name = "from_address")
    private String fromAddress;
    
    @Column(name = "to_address")
    private String toAddress;
    
    @NotNull
    private String subject;
    
    @NotNull
    private String body;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_state")
    private MessagingState messagingState;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_type")
    private MessagingType messagingType;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    public String getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress()
    {
        return toAddress;
    }

    public void setToAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }
    
    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
    
    public MessagingState getMessagingState()
    {
        return messagingState;
    }
    
    public void setMessagingState(MessagingState messagingState)
    {
        this.messagingState = messagingState;
    }
    
    public MessagingType getMessagingType()
    {
        return messagingType;
    }
    
    public void setMessagingType(MessagingType messagingType)
    {
        this.messagingType = messagingType;
    }
    
    public Date getCreationTime()
    {
        return creationTime;
    }
    
    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }
    
    public Helper getHelper()
    {
        return helper;
    }
    
    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+messagingType+"]";
    }
}