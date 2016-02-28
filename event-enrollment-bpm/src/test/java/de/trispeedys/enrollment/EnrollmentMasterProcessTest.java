package de.trispeedys.enrollment;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.enrollment.execution.BpmEnrollmentMessages;

public class EnrollmentMasterProcessTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    @Test
    @Deployment(resources = "EventEnrollmentMasterProcess.bpmn")
    public void test123()
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        processEngine.getRuntimeService().startProcessInstanceByMessage(BpmEnrollmentMessages.MSG_START_ENROLLMENT, null, variables);
    }
}