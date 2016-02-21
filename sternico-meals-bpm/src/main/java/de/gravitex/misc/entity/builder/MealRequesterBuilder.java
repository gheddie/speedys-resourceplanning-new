package de.gravitex.misc.entity.builder;

import de.gravitex.misc.entity.MealRequester;

public class MealRequesterBuilder extends AbstractMealEntityBuilder<MealRequester>
{
    public MealRequester build()
    {
        return new MealRequester();
    }
}