package de.trispeedys.resourceplanning.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import de.trispeedys.resourceplanning.entity.misc.HelperState;

@Entity
public class Helper extends AbstractDbObject
{
    public static final String ATTR_FIRST_NAME = "firstName";
    
    public static final String ATTR_LAST_NAME = "lastName";
    
    public static final String ATTR_HELPER_STATE = "helperState";

    public static final String ATTR_CODE = "code";   

    @Column(name = "first_name")
    @Length(min=2)
    private String firstName;
    
    @Column(name = "last_name")
    @Length(min=2)
    private String lastName;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    
    // @NotNull
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "helper_state")
    @NotNull
    private HelperState helperState;

    @NotNull
    private String code;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public HelperState getHelperState()
    {
        return helperState;
    }
    
    public void setHelperState(HelperState helperState)
    {
        this.helperState = helperState;
    }
    
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+lastName+", "+firstName+"]";
    }

    public boolean isActive()
    {
        return (helperState.equals(HelperState.ACTIVE));
    }

    public boolean isAssignableTo(Position pos, Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOfBirth);
        cal.add(Calendar.YEAR, pos.getMinimalAge());
        Date added = cal.getTime();
        return (date.after(added));
    }
}