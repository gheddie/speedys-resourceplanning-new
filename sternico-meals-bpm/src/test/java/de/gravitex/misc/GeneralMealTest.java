package de.gravitex.misc;

import static org.junit.Assert.assertEquals;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.gravitex.misc.datasource.MealRequesterDatasource;

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
        
        // there should be 3 requesters...
        assertEquals(3, new MealRequesterDatasource().findAll(null).size());
        
        // start a process
        processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_MEAL_OFFERED", null, null);
    }
}