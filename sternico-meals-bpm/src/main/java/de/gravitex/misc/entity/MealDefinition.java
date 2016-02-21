package de.gravitex.misc.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "meal_definition")
public class MealDefinition extends MealRequestDbObject
{
    private String description;
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
}