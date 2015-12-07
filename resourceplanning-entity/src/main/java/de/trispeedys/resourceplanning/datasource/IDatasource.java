package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.persistence.SessionToken;

public interface IDatasource
{
    public <T> T saveOrUpdate(SessionToken sessionToken, T dbObject);
    
    public <T> void remove(SessionToken sessionToken, T entity);
    
    public <T> T findById(SessionToken sessionToken, Long primaryKeyValue);
    
    public <T> List<T> find(SessionToken sessionToken, String qryString, HashMap<String, Object> parameters);
    
    public <T> T findSingle(SessionToken sessionToken, String qryString, HashMap<String, Object> parameters);
    
    public <T> List<T> find(SessionToken sessionToken, String qryString);
    
    public <T> T findSingle(SessionToken sessionToken, String qryString);

    public <T> List<T> find(SessionToken sessionToken, String qryString, String paramaterName, Object paramaterValue);
    
    public <T> T findSingle(SessionToken sessionToken, String qryString, String paramaterName, Object paramaterValue);

    public <T> List<T> findAll(SessionToken sessionToken);    

    public <T> List<T> find(SessionToken sessionToken, String paramaterName, Object paramaterValue);
    
    public <T> T findSingle(SessionToken sessionToken, String paramaterName, Object paramaterValue);
    
    public <T> List<T> find(SessionToken sessionToken, Object... filters);
    
    public <T> T findSingle(SessionToken sessionToken, Object... filters);
}