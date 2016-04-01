package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.template.helprequest.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class ProposePositionsDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // send a mail with all unassigned positions in the current event
        GuidedEvent event = getEvent(execution);
        Helper helper = getHelper(execution);
        List<Position> unassignedPositions =
                RepositoryProvider.getRepository(PositionRepository.class).findUnassignedPositionsByGenerator(helper,
                        event);
        if ((unassignedPositions == null) || (unassignedPositions.size() == 0))
        {
            throw new ResourcePlanningException("can not propose any unassigned positions as there are none!!");
        }
        // send mail
        boolean isReentrant = false;
        Object varReentrant = execution.getVariable(BpmVariables.RequestHelpHelper.VAR_POS_CHOOSING_REENTRANT);
        if (varReentrant != null)
        {
            if ((Boolean) varReentrant)
            {
                isReentrant = true;
            }
        }
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(helper, event, unassignedPositions,
                        (HelperCallback) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK),
                        RepositoryProvider.getRepository(HelperAssignmentRepository.class)
                                .getPriorAssignment(helper, event.getEventTemplate())
                                .getPosition(), isReentrant);
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                helper.getEmail(), template.constructSubject(), template.constructBody(), template.getMessagingType(),
                true, helper);
    }
}