package de.gravitex.misc.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "meal_proposal")
public class MealProposal extends MealRequestDbObject
{
    @ManyToOne
    @NotNull
    @JoinColumn(name = "meal_definition_id")
    private MealDefinition mealDefinition;
    
    public MealDefinition getMealDefinition()
    {
        return mealDefinition;
    }
    
    public void setMealDefinition(MealDefinition mealDefinition)
    {
        this.mealDefinition = mealDefinition;
    }
}