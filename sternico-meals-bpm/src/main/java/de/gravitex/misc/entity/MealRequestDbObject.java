package de.gravitex.misc.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import de.gravitex.hibernateadapter.core.DbObject;

@MappedSuperclass
public class MealRequestDbObject implements DbObject
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
    
    public boolean isNew()
    {
        return (id == null);
    }
}