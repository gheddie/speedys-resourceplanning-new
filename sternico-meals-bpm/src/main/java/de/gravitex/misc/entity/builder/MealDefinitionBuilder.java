package de.gravitex.misc.entity.builder;

import de.gravitex.misc.entity.MealDefinition;

public class MealDefinitionBuilder extends AbstractMealEntityBuilder<MealDefinition>
{
    public MealDefinition build()
    {
        return new MealDefinition();
    }
}