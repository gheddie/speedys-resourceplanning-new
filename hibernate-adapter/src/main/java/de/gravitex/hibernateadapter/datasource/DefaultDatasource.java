package de.gravitex.hibernateadapter.datasource;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.core.DbObject;
import de.gravitex.hibernateadapter.core.IDatasource;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;

public class DefaultDatasource<T> implements IDatasource
{
    @SuppressWarnings({
            "rawtypes", "unchecked", "hiding"
    })
    public <T> List<T> find(SessionToken sessionToken, String qryString, HashMap<String, Object> parameters)
    {
        Session session = SessionManager.getInstance().getSession(sessionToken);
        Query q = session.createQuery(qryString);
        if (parameters != null)
        {
            for (String key : parameters.keySet())
            {
                q.setParameter(key, parameters.get(key));
            }
        }
        List result = q.list();
        if (sessionToken == null)
        {
            // 'single use' session, so close it...            
            unregisterSession(session);  
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> find(SessionToken sessionToken, String qryString)
    {
        return (List<T>) find(sessionToken, qryString, null);
    }

    @SuppressWarnings("unchecked")
    public List<T> find(SessionToken sessionToken, String qryString, String paramaterName, Object paramaterValue)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramaterName, paramaterValue);
        return find(sessionToken, qryString, parameters);
    }

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> List<T> find(SessionToken sessionToken, String paramaterName, Object paramaterValue)
    {
        return (List<T>) find(sessionToken, "FROM " +
                getGenericType().getSimpleName() + " WHERE " + paramaterName + " = :" + paramaterName, paramaterName,
                paramaterValue);
    }
    
    @SuppressWarnings("hiding")
    public <T> void remove(SessionToken sessionToken, T entity)
    {
        if (sessionToken != null)
        {
            // session token set --> use registered session to remove...
            SessionManager.getInstance().getSession(sessionToken).delete(entity);
        }
        else
        {
            // no session token --> do remove in an isolated transaction
            Transaction tx = null;
            Session session = SessionManager.getInstance().getSession(sessionToken);
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
            
            // 'single use' session, so close it...
            unregisterSession(session);            
        }     
    }
    
    @SuppressWarnings("hiding")
    public <T> T saveOrUpdate(SessionToken sessionToken, T entity)
    {
        if (sessionToken != null)
        {
            // session token set --> use registered session to save or update...
            if (((DbObject) entity).isNew())
            {
                SessionManager.getInstance().getSession(sessionToken).save(entity);   
            }
            else
            {
                SessionManager.getInstance().getSession(sessionToken).update(entity);
            }            
            return (T) entity;
        }
        else
        {
            // no session token --> do save or update in an isolated transaction
            Transaction tx = null;
            Session session = SessionManager.getInstance().getSession(null);
            tx = session.beginTransaction();
            if (((DbObject) entity).isNew())
            {
                session.save(entity);
            }
            else
            {
                session.update(entity);
            }
            tx.commit();
            
            // 'single use' session, so close it...
            unregisterSession(session);
            
            return (T) entity;   
        }
    }    

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> List<T> findAll(SessionToken sessionToken)
    {
        return (List<T>) find(sessionToken, "FROM " + getGenericType().getSimpleName());
    }
    
    @SuppressWarnings({
            "hiding", "unchecked"
    })
    public <T> List<T> find(SessionToken sessionToken, Object... filters)
    {
        if ((filters == null) || (filters.length == 0))
        {
            // no filters
            return (List<T>) findAll(sessionToken);
        }
        if (!(filters.length % 2 == 0))
        {
            // odd number of filters arguments
            throw new IllegalArgumentException("odd number of filters arguments ("+filters.length+")!");
        }
        if (filters.length == 2)
        {
            // one key value pair
            return find(sessionToken, getGenericType(), filters[0], filters[1]);
        }
        String qryString = "FROM " + getGenericType().getSimpleName() + " WHERE (";
        for (int index = 0; index < filters.length; index += 2)
        {
            qryString += filters[index] + " = :" + filters[index];
            if (index < filters.length - 2)
            {
                qryString += " AND ";   
            }            
        }
        qryString += ")";        
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        for (int index = 0; index < filters.length; index += 2)
        {
            parameters.put((String) filters[index], filters[index+1]);
        }
        return find(sessionToken, qryString, parameters);
    }
    
    @SuppressWarnings("hiding")
    public <T> T findSingle(SessionToken sessionToken, String qryString, HashMap<String, Object> parameters)
    {
        List<T> result = find(sessionToken, qryString, parameters);
        assertSingle(result);
        return result.get(0);
    }

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> T findSingle(SessionToken sessionToken, String qryString)
    {
        List<T> result = (List<T>) find(sessionToken, qryString);
        assertSingle(result);
        return result.get(0);
    }

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> T findSingle(SessionToken sessionToken, String qryString, String paramaterName, Object paramaterValue)
    {
        List<T> result = (List<T>) find(sessionToken, qryString, paramaterName, paramaterValue);
        assertSingle(result);
        return result.get(0);
    }

    @SuppressWarnings("hiding")
    public <T> T findSingle(SessionToken sessionToken, String paramaterName, Object paramaterValue)
    {
        List<T> result = find(sessionToken, paramaterName, paramaterValue);
        assertSingle(result);
        return result.get(0);
    }

    @SuppressWarnings("hiding")
    public <T> T findSingle(SessionToken sessionToken, Object... filters)
    {
        List<T> result = find(sessionToken, filters);
        assertSingle(result);
        return result.get(0);
    }    
    
    private void assertSingle(List<?> result)
    {
        if ((result == null) || (result.size() != 1))
        {
            throw new IllegalArgumentException("single result excepted");
        }
    }
    
    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> T findById(SessionToken sessionToken, Long primaryKeyValue)
    {
        if (primaryKeyValue == null)
        {
            return null;
        }
        List<T> list = (List<T>) find(sessionToken, "FROM " + getGenericType().getSimpleName() + " WHERE id = " + primaryKeyValue);
        return (list != null && list.size() == 1 ? (T) list.get(0) : null);
    }

    /**
     * gets the generic type of the datasource instance (inherit of {@link DbObject})
     * @return
     */
    protected Class<T> getGenericType()
    {
        return null;
    }
    
    private void unregisterSession(Session session)
    {
        SessionManager.getInstance().unregisterSession(session);
    }
}