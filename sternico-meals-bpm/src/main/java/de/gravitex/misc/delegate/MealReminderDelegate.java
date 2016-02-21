package de.gravitex.misc.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class MealReminderDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // (1) create db entry
    }
}