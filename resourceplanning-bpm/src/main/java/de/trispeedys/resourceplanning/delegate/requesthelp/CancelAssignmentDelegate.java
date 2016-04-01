package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.messaging.template.helprequest.CancelConfirmationMailTemplate;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;

public class CancelAssignmentDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // cancel assignment
        GuidedEvent event = getEvent(execution);
        Helper helper = getHelper(execution);
        RepositoryProvider.getRepository(HelperAssignmentRepository.class).cancelHelperAssignment(helper, event);

        // trigger cancellation in event manager
        EventManager.onAssignmentCancelled(event.getId(),
                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));

        // send confirmation
        CancelConfirmationMailTemplate template =
                new CancelConfirmationMailTemplate(getHelper(execution), getEvent(execution),
                        RepositoryProvider.getRepository(PositionRepository.class).findById(
                                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)));
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                helper.getEmail(), template.constructSubject(), template.constructBody(), template.getMessagingType(),
                true, helper);
    }
}