package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.util.StringUtil;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Person extends AbstractDbObject
{
    public static final String ATTR_FIRST_NAME = "firstName";

    public static final String ATTR_LAST_NAME = "lastName";
    
    public static final String ATTR_CODE = "code";

    public static final String ATTR_MAIL_ADDRESS = "email";
    
    @Column(name = "first_name")
    @Length(min = 2)
    private String firstName;

    @Column(name = "last_name")
    @Length(min = 2)
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    // @NotNull
    private String email;
    
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
    
    public boolean isValid()
    {
        return ((!(StringUtil.isBlank(firstName))) && (!(StringUtil.isBlank(lastName))));
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " [" + lastName + ", " + firstName + "]";
    }
}