package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.gravitex.hibernateadapter.entity.AbstractDbObject;

@Entity
@Table(name = "app_configuration")
public class AppConfigurationEntry extends AbstractDbObject
{
    private String key;
    
    private String value;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}