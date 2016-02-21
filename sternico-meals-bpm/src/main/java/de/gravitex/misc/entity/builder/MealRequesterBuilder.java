package de.gravitex.misc.entity.builder;

import de.gravitex.misc.entity.MealRequester;

public class MealRequesterBuilder extends AbstractMealEntityBuilder<MealRequester>
{
    private String lastName;
    
    private String firstName;
    
    private String mailAddress;

    public MealRequesterBuilder withFirstName(String aFirstName)
    {
        firstName = aFirstName;
        return this;
    }

    public MealRequesterBuilder withLastName(String aLastName)
    {
        lastName = aLastName;
        return this;
    }
    
    public MealRequesterBuilder withMailAddress(String aMailAddress)
    {
        mailAddress = aMailAddress;
        return this;
    }
    
    public MealRequester build()
    {
        MealRequester mealRequester = new MealRequester();
        mealRequester.setLastName(lastName);
        mealRequester.setFirstName(firstName);
        mealRequester.setMailAddress(mailAddress);
        return mealRequester;
    }
}