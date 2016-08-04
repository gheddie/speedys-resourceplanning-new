package de.gravitex.hibernateadapter.datasource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.gravitex.hibernateadapter.core.DbObject;
import de.gravitex.hibernateadapter.core.IDatasource;
import de.gravitex.hibernateadapter.core.SessionManager;
import de.gravitex.hibernateadapter.core.SessionToken;
import de.gravitex.hibernateadapter.core.annotation.DbOperationType;
import de.gravitex.hibernateadapter.core.annotation.EntitySaveListener;
import de.gravitex.hibernateadapter.core.exception.HibernateAdapterException;
import de.gravitex.hibernateadapter.entity.AbstractDbObject;

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
            setQueryParameters(q, parameters);
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

    public int executeQuery(SessionToken sessionToken, String command, HashMap<String, Object> parameters)
    {
        if (sessionToken != null)
        {
            // session token set --> use registered session to execute...
            Query query = SessionManager.getInstance().getSession(sessionToken).createQuery(command);
            setQueryParameters(query, parameters);
            return query.executeUpdate();
        }
        else
        {
            // no session token --> execute command in an isolated transaction
            Transaction tx = null;
            Session session = SessionManager.getInstance().getSession(null);
            tx = session.beginTransaction();
            Query query = session.createQuery(command);   
            setQueryParameters(query, parameters);
            int count = query.executeUpdate();
            tx.commit();
            
            // 'single use' session, so close it...
            unregisterSession(session);
            
            return count;
        }
    }
    
    @SuppressWarnings("hiding")
    public <T> T saveOrUpdate(SessionToken sessionToken, T entity)
    {
        if (sessionToken != null)
        {
            // session token set --> use registered session to save or update...
            Session attachedSession = SessionManager.getInstance().getSession(sessionToken);
            
            ((AbstractDbObject) entity).setSessionToken(sessionToken);
            
            if (((DbObject) entity).isNew())
            {
                checkSaveTriggers((AbstractDbObject) entity);
                attachedSession.save(entity);   
            }
            else
            {
                checkUpdateTriggers((AbstractDbObject) entity);
                attachedSession.update(entity);
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
                checkSaveTriggers((AbstractDbObject) entity);
                session.save(entity);
            }
            else
            {
                checkUpdateTriggers((AbstractDbObject) entity);
                session.update(entity);
            }
            tx.commit();
            
            // 'single use' session, so close it...
            unregisterSession(session);
            
            return (T) entity;   
        }
    }    
    
    private void checkSaveTriggers(AbstractDbObject entity)
    {
        for (Method method : entity.getClass().getDeclaredMethods())
        {
            if (hasSaveListener(method))
            {
                try
                {
                    if (!((Boolean) method.invoke(entity, new Object[]{})))
                    {
                        throw new HibernateAdapterException(replaceParameters(method.getAnnotation(EntitySaveListener.class).errorKey()));
                    }
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void checkUpdateTriggers(AbstractDbObject entity)
    {
        for (Method method : entity.getClass().getDeclaredMethods())
        {
            if (hasUpdateListener(method))
            {
                try
                {
                    if (!((Boolean) method.invoke(entity, new Object[]{})))
                    {
                        throw new HibernateAdapterException(replaceParameters(method.getAnnotation(EntitySaveListener.class).errorKey()));
                    }   
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }            
            }
        }
    }
    
    private String replaceParameters(String errorKey)
    {
        if ((errorKey == null) || (errorKey.length() == 0))
        {
            return errorKey;
        }
        errorKey.indexOf('@');
        return null;
    }
    
    private boolean hasSaveListener(Method method)
    {
        EntitySaveListener listener = method.getAnnotation(EntitySaveListener.class);
        if (listener == null)
        {
            return false;
        }
        return ((listener.operationType().equals(DbOperationType.PERSIST)) || (listener.operationType().equals(DbOperationType.PERSIST_AND_UPDATE)));
    }

    private boolean hasUpdateListener(Method method)
    {
        EntitySaveListener listener = method.getAnnotation(EntitySaveListener.class);
        if (listener == null)
        {
            return false;
        }
        return ((listener.operationType().equals(DbOperationType.UPDATE)) || (listener.operationType().equals(DbOperationType.PERSIST_AND_UPDATE)));
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
        return extractSingleValue(result);
    }

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> T findSingle(SessionToken sessionToken, String qryString)
    {
        List<T> result = (List<T>) find(sessionToken, qryString);
        assertSingle(result);
        return extractSingleValue(result);
    }

    @SuppressWarnings({
            "unchecked", "hiding"
    })
    public <T> T findSingle(SessionToken sessionToken, String qryString, String paramaterName, Object paramaterValue)
    {
        List<T> result = (List<T>) find(sessionToken, qryString, paramaterName, paramaterValue);
        assertSingle(result);
        return extractSingleValue(result);
    }

    @SuppressWarnings("hiding")
    public <T> T findSingle(SessionToken sessionToken, String paramaterName, Object paramaterValue)
    {
        List<T> result = find(sessionToken, paramaterName, paramaterValue);
        assertSingle(result);
        return extractSingleValue(result);
    }

    @SuppressWarnings("hiding")
    public <T> T findSingle(SessionToken sessionToken, Object... filters)
    {
        List<T> result = find(sessionToken, filters);
        assertSingle(result);
        return extractSingleValue(result);
    }

    private <T> T extractSingleValue(List<T> result)
    {
        if ((result == null) || (result.size() == 0))
        {
            return null;
        }
        return result.get(0);
    }    
    
    private void assertSingle(List<?> result)
    {
        if (result == null)
        {
            // nothing found --> OK
            return;
        }
        else
        {
            if ((result.size() == 0) || (result.size() == 1))
            {
                // 0 or 1 row found --> OK
                return;
            }
        }
        throw new IllegalArgumentException("single result excepted, found : ["+result.size()+"] items!!");
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

    private void setQueryParameters(Query query, HashMap<String, Object> parameters)
    {
        if ((parameters == null) || (parameters.size() == 0))
        {
            return;
        }
        for (String key : parameters.keySet())
        {
            query.setParameter(key, parameters.get(key));
        }
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