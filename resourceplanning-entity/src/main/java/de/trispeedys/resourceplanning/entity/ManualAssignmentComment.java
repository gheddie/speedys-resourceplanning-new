package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.util.StringUtil;

@Entity
@Table(name = "manual_assignment_comment")
public class ManualAssignmentComment extends AbstractDbObject
{
    public static final String ATTR_HELPER = "helper";

    public static final String ATTR_EVENT = "event";

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helper_id")
    private Helper helper;
    
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;
    
    private String comment;
    
    public String getComment()
    {
        return comment;
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
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean isFilled()
    {
        return (!(StringUtil.isBlank(comment)));
    }
}