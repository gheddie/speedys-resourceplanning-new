package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.gravitex.hibernateadapter.core.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;
import de.trispeedys.resourceplanning.repository.MessageQueueRepository;

/**
 * super class for mail sending delegates using a {@link AbstractMailTemplate}.
 * 
 * @author Stefan.Schulz
 *
 */
public abstract class RequestHelpNotificationDelegate extends AbstractRequestHelpDelegate
{
    private static final String MAIL_TEMPLATE_POSTFIX = "MailTemplate";

    private static final String DELEGATE_POSTFIX = "Delegate";

    private static final String TEMPLATE_DIRECTORY = "de.trispeedys.resourceplanning.messaging.template.helprequest";

    @SuppressWarnings("rawtypes")
    protected void constructMessage(DelegateExecution execution, Helper helper, Position position, Event event,
            String toAddress)
    {
        RequestHelpMailTemplate template = getMessageTemplate(execution, helper, position, event);
        // TODO pass 'null' as parameter 'helper' (--> how can we decide for whom [helper/admin] this mail is?)
        RepositoryProvider.getRepository(MessageQueueRepository.class).createMessage("noreply@tri-speedys.de",
                toAddress, template.constructSubject(), template.constructBody(), template.getMessagingType(), true,
                null);
    }

    @SuppressWarnings("rawtypes")
    protected RequestHelpMailTemplate getMessageTemplate(DelegateExecution execution, Helper helper, Position position,
            Event event)
    {
        String baseName = getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf(DELEGATE_POSTFIX));
        String templateName = TEMPLATE_DIRECTORY + "." + baseName + MAIL_TEMPLATE_POSTFIX;
        try
        {
            RequestHelpMailTemplate template = (RequestHelpMailTemplate) Class.forName(templateName).newInstance();
            template.setHelper(helper);
            template.setEvent(event);
            template.setPosition(position);
            return template;
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            throw new ResourcePlanningException("unable to construct message template for class '" +
                    getClass().getSimpleName() + "' : " + e.getMessage());
        }
    }
}