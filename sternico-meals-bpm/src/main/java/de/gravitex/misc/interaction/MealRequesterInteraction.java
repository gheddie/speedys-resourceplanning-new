package de.gravitex.misc.interaction;

import java.util.HashMap;

import org.camunda.bpm.engine.ProcessEngine;

import de.gravitex.misc.execution.BpmMealMessages;

public class MealRequesterInteraction
{
    public static synchronized String processRequesterCallback(Long requesterId, ProcessEngine testEngine)
    {
        String businessKey = null;
        testEngine.getRuntimeService().correlateMessage(BpmMealMessages.MSG_CALLBACK_RECEIVED, businessKey, new HashMap<String, Object>());
        return null;        
    }
}