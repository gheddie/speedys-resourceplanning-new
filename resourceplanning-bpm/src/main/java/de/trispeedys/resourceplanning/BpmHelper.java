package de.trispeedys.resourceplanning;

import java.util.List;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Execution;

import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;

public class BpmHelper
{
    public static boolean checkMessageSubscription(Long eventId, Long helperId, String messageName, ProcessEngine testEngine)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        List<Execution> result = getProcessEngine(testEngine).getRuntimeService().createExecutionQuery().processInstanceBusinessKey(businessKey).messageEventSubscriptionName(messageName).list();
        return ((result != null) && (result.size() > 0));
    }

    public static ProcessEngine getProcessEngine(ProcessEngine testEngine)
    {
        // return test engine if set, default engine from bpm platform otherwise
        return testEngine != null
                ? testEngine
                : BpmPlatform.getDefaultProcessEngine();
    }
}