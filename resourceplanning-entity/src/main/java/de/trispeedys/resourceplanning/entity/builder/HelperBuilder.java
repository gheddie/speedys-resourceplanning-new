package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

public class HelperBuilder extends AbstractEntityBuilder<Helper>
{
    private String firstName;
    
    private String lastName;

    private Date dateOfBirth;

    private String email;

    private HelperState helperState;

    public HelperBuilder withFirstName(String aFirstName)
    {
        firstName = aFirstName;
        return this;
    }

    public HelperBuilder withLastName(String aLastName)
    {
        lastName = aLastName;
        return this;
    }
    
    public HelperBuilder withDateOfBirth(Date aDateOfBirth)
    {
        dateOfBirth = aDateOfBirth;
        return this;
    }
    
    public HelperBuilder withHelperState(HelperState aHelperState)
    {
        helperState = aHelperState;
        return this;
    }
    
    public HelperBuilder withEmail(String aEmail)
    {
        email = aEmail;
        return this;
    }

    public Helper build()
    {
        Helper helper = new Helper();
        helper.setFirstName(firstName);
        helper.setLastName(lastName);
        helper.setDateOfBirth(dateOfBirth);
        helper.setEmail(email);
        helper.setHelperState(helperState);
        return helper;
    }
}