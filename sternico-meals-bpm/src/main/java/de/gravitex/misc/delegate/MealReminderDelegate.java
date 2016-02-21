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
        
        // (1) create db entry
    }
}