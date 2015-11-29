package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.CancelConfirmationMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.MessagingService;

public class CancelAssignmentDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // cancel assignment
        Event event = getEvent(execution);
        Helper helper = getHelper(execution);
        AssignmentService.cancelHelperAssignment(helper, event);
        
        // send confirmation
        CancelConfirmationMailTemplate template =
                new CancelConfirmationMailTemplate(getHelper(execution), getEvent(execution), RepositoryProvider.getRepository(
                        PositionRepository.class).findById(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)));
        MessagingService.createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(), template.constructBody(),
                template.getMessagingType(), template.getMessagingFormat());
    }
}