package de.gravitex.hibernateadapter.core.repository;

import java.lang.reflect.Method;
import java.util.List;

import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.exception.HibernateAdapterException;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;

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
        return findAll(null);
    }

    public List<T> findAll(SessionToken sessionToken)
    {
        return dataSource.findAll(sessionToken);
    }

    public T findById(Long id)
    {
        return dataSource.findById(null, id);
    }
    
    public void saveOrUpdate(SessionToken sessionToken, T entity)
    {
        dataSource.saveOrUpdate(sessionToken, entity);
    }

    public void saveOrUpdate(T entity)
    {
        saveOrUpdate(null, entity);
    }
    
    public void remove(T entity)
    {
        dataSource.remove(null, entity);
    }

    public void updateSingleValue(T entity, String attributeName, Object newValue)
    {
        if (entity == null)
        {
            throw new IllegalArgumentException("entity must not be NULL!!");
        }
        if ((attributeName == null) || (attributeName.length() == 0))
        {
            throw new IllegalArgumentException("attribute name must be set!!");
        }        
        try
        {
            Method setter = entity.getClass().getDeclaredMethod(setterName(attributeName), new Class[]
            {
                newValue.getClass()
            });
            setter.invoke(entity, newValue);
            dataSource.saveOrUpdate(null, entity);
        }
        catch (Exception e)
        {
            throw new HibernateAdapterException("could not update value of attribute '" +
                    attributeName + "' in a bean of class '" + entity.getClass().getSimpleName() + "' : ", e);
        }
    }
    
    private String setterName(String attribute)
    {
        String tmp = "";
        tmp += attribute.charAt(0);
        tmp = tmp.toUpperCase();
        tmp += attribute.substring(1, attribute.length());
        return "set" + tmp;
    }    

    public DefaultDatasource<T> dataSource()
    {
        return dataSource;
    }
    
    protected T safeValue(List<T> result)
    {
        if ((result != null) && (result.size() > 1))
        {
            // this is an exception...method is only meant to give by a single value or NULL...
            throw new IllegalArgumentException();
        }
        return (T) (result == null || result.size() == 0 ? null : result.get(0));
    }
}