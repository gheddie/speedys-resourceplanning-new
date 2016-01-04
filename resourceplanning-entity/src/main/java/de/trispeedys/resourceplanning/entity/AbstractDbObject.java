package de.trispeedys.resourceplanning.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.gravitex.hibernateadapter.core.DbObject;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.Datasources;

@MappedSuperclass
public abstract class AbstractDbObject implements DbObject
{
    @Id
    @GeneratedValue
    private Long id;
    
    public Long getId()
    {
        return id;
    }
    
    protected void setId(Long id)
    {
        this.id = id;
    }
    
    public void remove()
    {
        remove(null);
    }
    
    @SuppressWarnings("unchecked")
    public <T> void remove(SessionToken sessionToken)
    {
        DefaultDatasource<T> datasource = (DefaultDatasource<T>) Datasources.getDatasource(getClass());
        datasource.remove(sessionToken, this);
    }    
    
    public <T> T saveOrUpdate()
    {
        return saveOrUpdate(null);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T saveOrUpdate(SessionToken sessionToken)
    {
        DefaultDatasource<T> datasource = (DefaultDatasource<T>) Datasources.getDatasource(getClass());
        return (T) datasource.saveOrUpdate(sessionToken, this);
    }

    public boolean isNew()
    {
        return (id == null);
    }
}