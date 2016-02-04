package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.AbstractRequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class CheckAvailabilityDelegate extends AbstractRequestHelpDelegate
{
    private static final Logger logger = Logger.getLogger(CheckAvailabilityDelegate.class);

    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        Event event = getEvent(execution);
        Position position =
                RepositoryProvider.getRepository(HelperAssignmentRepository.class)
                        .getPriorAssignment(helper, event.getEventTemplate())
                        .getPosition();
        boolean positionAvailable =
                RepositoryProvider.getRepository(PositionRepository.class).isPositionAvailable(event, position);
        logger.info("ckecking availability for helper '" +
                helper + "' and position '" + position + "' in event '" + event + "', position available : " +
                positionAvailable + " [bk:" + execution.getBusinessKey() + "]");

        // set id of the position and the information, if the prior position is avaiablable...
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE, positionAvailable);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
    }
}