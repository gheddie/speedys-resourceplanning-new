package de.gravitex.misc.repository;

import de.gravitex.hibernateadapter.core.repository.AbstractDatabaseRepository;
import de.gravitex.hibernateadapter.core.repository.DatabaseRepository;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.gravitex.misc.datasource.MealDefinitionDatasource;
import de.gravitex.misc.entity.MealDefinition;

public class MealDefinitionRepository extends AbstractDatabaseRepository<MealDefinition> implements DatabaseRepository<MealDefinitionRepository>
{
    protected DefaultDatasource<MealDefinition> createDataSource()
    {
        return new MealDefinitionDatasource();
    }
}