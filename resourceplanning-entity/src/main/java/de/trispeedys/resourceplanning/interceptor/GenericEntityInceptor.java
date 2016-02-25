package de.trispeedys.resourceplanning.interceptor;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

public class GenericEntityInceptor extends EmptyInterceptor
{
    private static final long serialVersionUID = 1L;

    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
    {
        ((AbstractDbObject) entity).checkSave();
        return super.onSave(entity, id, state, propertyNames, types);
    }
}