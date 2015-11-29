package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;

public class CheckAvailabilityInitialDelegate extends RequestHelpDelegate
{
    private static final Logger logger = Logger.getLogger(CheckAvailabilityInitialDelegate.class);

    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        Event event = getEvent(execution);
        Position position = AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition();
        boolean positionAvailable = PositionService.isPositionAvailable(event, position);
        logger.info("ckecking availability for helper '" +
                helper + "' and position '" + position + "' in event '" + event + "', position available : " + positionAvailable + " [bk:" +
                execution.getBusinessKey() + "]");
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE, positionAvailable);

        // set id of the position
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION, position.getId());
    }
}