package de.gravitex.misc.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.misc.delegate.base.AbstractMealRequestDelegate;
import de.gravitex.misc.execution.BpmMealVariables;

public class MealReminderDelegate extends AbstractMealRequestDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // there must be a meal definition set...
        checkVariable(BpmMealVariables.VAR_MEAL_DEF_ID, execution);
        
        // there must a number of available places set...
        checkVariable(BpmMealVariables.VAR_AVAILABLE_PLACES, execution);
        
        // set reservations to zero...
        execution.setVariable(BpmMealVariables.VAR_RESERVATION_COUNT, 0);
        
        // (1) create db entry
    }
}