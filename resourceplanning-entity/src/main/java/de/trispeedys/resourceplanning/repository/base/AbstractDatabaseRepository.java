package de.trispeedys.resourceplanning.repository.base;

import java.lang.reflect.Method;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningPersistenceException;

public abstract class AbstractDatabaseRepository<T>
{
    private DefaultDatasource<T> dataSource;

    public AbstractDatabaseRepository()
    {
        super();
        dataSource = createDataSource();
    }

    protected abstract DefaultDatasource<T> createDataSource();

    public List<T> findAll()
    {
        return dataSource.findAll();
    }

    public T findById(Long id)
    {
        return dataSource.findById(id);
    }

    public void saveOrUpdate(T entity)
    {
        dataSource.saveOrUpdate(entity);
    }

    public void updateSingleValue(T entity, String attributeName, Object newValue)
    {
        if (entity == null)
        {
            throw new ResourcePlanningPersistenceException("entity must not be NULL!!");
        }
        if (StringUtil.isBlank(attributeName))
        {
            throw new ResourcePlanningPersistenceException("attribute name must not be set!!");
        }        
        try
        {
            Method setter = entity.getClass().getDeclaredMethod(StringUtil.setterName(attributeName), new Class[]
            {
                newValue.getClass()
            });
            setter.invoke(entity, newValue);
            dataSource.saveOrUpdate(entity);
        }
        catch (Exception e)
        {
            throw new ResourcePlanningPersistenceException("could not update value of attribute '" +
                    attributeName + "' in a bean of class '" + entity.getClass().getSimpleName() + "' : ", e);
        }
    }

    public DefaultDatasource<T> dataSource()
    {
        return dataSource;
    }
}