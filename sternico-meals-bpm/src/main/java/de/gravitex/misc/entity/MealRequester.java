package de.gravitex.misc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "meal_requester")
public class MealRequester extends MealRequestDbObject
{
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "mail_address")
    private String mailAddress;
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public String getMailAddress()
    {
        return mailAddress;
    }
    
    public void setMailAddress(String mailAddress)
    {
        this.mailAddress = mailAddress;
    }
}