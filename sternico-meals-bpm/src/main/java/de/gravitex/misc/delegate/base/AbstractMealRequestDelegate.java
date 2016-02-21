package de.gravitex.misc.delegate.base;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.gravitex.misc.exception.MealRequestException;

public abstract class AbstractMealRequestDelegate implements JavaDelegate
{
    protected void checkVariable(String variableName, DelegateExecution execution)
    {
         if (execution.getVariable(variableName) == null)
         {
             throw new MealRequestException("given variable '"+variableName+"' must not be NULL at this point!");
         }
    }
}