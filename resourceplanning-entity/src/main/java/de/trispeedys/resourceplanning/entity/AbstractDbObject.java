package de.trispeedys.resourceplanning.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.persistence.SessionToken;

@MappedSuperclass
public abstract class AbstractDbObject
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