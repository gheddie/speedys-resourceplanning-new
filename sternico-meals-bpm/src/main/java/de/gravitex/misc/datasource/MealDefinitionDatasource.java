package de.gravitex.misc.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.gravitex.misc.entity.MealDefinition;

public class MealDefinitionDatasource extends DefaultDatasource<MealDefinition>
{
    protected Class<MealDefinition> getGenericType()
    {
        return MealDefinition.class;
    }
}