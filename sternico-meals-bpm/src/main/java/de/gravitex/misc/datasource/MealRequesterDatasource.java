package de.gravitex.misc.datasource;

import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.gravitex.misc.entity.MealRequester;

public class MealRequesterDatasource extends DefaultDatasource<MealRequester>
{
    protected Class<MealRequester> getGenericType()
    {
        return MealRequester.class;
    }
}