package de.gravitex.misc;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.gravitex.misc.datasource.MealDefinitionDatasource;
import de.gravitex.misc.datasource.MealRequesterDatasource;
import de.gravitex.misc.entity.MealDefinition;
import de.gravitex.misc.entity.MealRequester;
import de.gravitex.misc.execution.BpmMealVariables;
import de.gravitex.misc.interaction.MealRequesterInteraction;
import de.gravitex.misc.repository.MealDefinitionRepository;

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
        
        MealTestUtil.setupDefinitions();
        
        // there should be 3 requesters...
        List<MealRequester> allRequesters = new MealRequesterDatasource().findAll(null);
        assertEquals(3, allRequesters.size());
        
        // load a definition in order to pass to the process...
        // MealDefinition definition = (MealDefinition) new MealDefinitionDatasource().findAll(null).get(0);
        MealDefinition definition = RepositoryProvider.getRepository(MealDefinitionRepository.class).findAll().get(0);
                
        // start a process
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmMealVariables.VAR_MEAL_DEF_ID, definition.getId());
        variables.put(BpmMealVariables.VAR_AVAILABLE_PLACES, 4);
        processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_MEAL_OFFERED", null, variables);
        
        // there are some requester callbacks...
        MealRequesterInteraction.processRequesterCallback(allRequesters.get(0).getId(), processEngine.getProcessEngine());
        MealRequesterInteraction.processRequesterCallback(allRequesters.get(1).getId(), processEngine.getProcessEngine());
    }
}