package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;

public class PreplanHelperDelegate extends AbstractRequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).confirmHelperAssignment(getHelper(execution), getEvent(execution));
    }
}