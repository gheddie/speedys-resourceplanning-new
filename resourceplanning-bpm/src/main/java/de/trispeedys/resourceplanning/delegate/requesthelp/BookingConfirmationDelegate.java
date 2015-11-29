package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.BookingConfirmationMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.MessagingService;

public class BookingConfirmationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        BookingConfirmationMailTemplate template =
                new BookingConfirmationMailTemplate(getHelper(execution), getEvent(execution), RepositoryProvider.getRepository(
                        PositionRepository.class).findById(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)));
        MessagingService.createMessage("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(), template.constructBody(),
                template.getMessagingType(), template.getMessagingFormat());
    }
}