package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public abstract class SpeedyProcessDelegate implements JavaDelegate
{
    protected Event getEvent(DelegateExecution execution)
    {
        Long eventId = (Long) execution.getVariable(BpmVariables.Misc.VAR_EVENT_ID);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '"+eventId+"' could not be found!!");
        }
        return event;
    }
}