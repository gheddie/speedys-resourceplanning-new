package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

public interface IDatasource
{
    public <T> T saveOrUpdate(T dbObject);
    
    public <T> void remove(T entity);
    
    public <T> T findById(Long primaryKeyValue);
    
    public <T> List<T> find(String qryString, HashMap<String, Object> parameters);
    
    public <T> T findSingle(String qryString, HashMap<String, Object> parameters);
    
    public <T> List<T> find(String qryString);
    
    public <T> T findSingle(String qryString);

    public <T> List<T> find(String qryString, String paramaterName, Object paramaterValue);
    
    public <T> T findSingle(String qryString, String paramaterName, Object paramaterValue);

    public <T> List<T> findAll();    

    public <T> List<T> find(String paramaterName, Object paramaterValue);
    
    public <T> T findSingle(String paramaterName, Object paramaterValue);
    
    public <T> List<T> find(Object... filters);
    
    public <T> T findSingle(Object... filters);
}