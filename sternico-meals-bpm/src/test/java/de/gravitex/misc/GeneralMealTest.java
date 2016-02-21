package de.gravitex.misc;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class GeneralMealTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    @Test
    @Deployment(resources = "MealRequest.bpmn")
    public void test123()
    {
        // clear db
        MealTestUtil.clearAll();
        
        MealTestUtil.setupRequesters();
        
        processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_MEAL_OFFERED", null, null);
    }
}