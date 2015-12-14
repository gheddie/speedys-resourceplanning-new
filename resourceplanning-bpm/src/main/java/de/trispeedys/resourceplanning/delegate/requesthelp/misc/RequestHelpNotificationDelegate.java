package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

/**
 * super class for mail sending delegates using a {@link AbstractMailTemplate}.
 * 
 * @author Stefan.Schulz
 *
 */
public abstract class RequestHelpNotificationDelegate extends RequestHelpDelegate
{
    private static final String MAIL_TEMPLATE_POSTFIX = "MailTemplate";

    private static final String DELEGATE_POSTFIX = "Delegate";

    private static final String TEMPLATE_DIRECTORY = "de.trispeedys.resourceplanning.messaging.template";

    @SuppressWarnings("rawtypes")
    protected void constructMessage(DelegateExecution execution, Helper helper, Position position, Event event,
            String toAddress)
    {
        AbstractMailTemplate template = getMessageTemplate(execution, helper, position, event);
        MessagingService.createMessage("noreply@tri-speedys.de", toAddress, template.constructSubject(),
                template.constructBody(), template.getMessagingType(), template.getMessagingFormat(), true);
    }

    @SuppressWarnings("rawtypes")
    private AbstractMailTemplate<?> getMessageTemplate(DelegateExecution execution, Helper helper, Position position,
            Event event)
    {
        String baseName = getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf(DELEGATE_POSTFIX));
        String templateName = TEMPLATE_DIRECTORY + "." + baseName + MAIL_TEMPLATE_POSTFIX;
        try
        {
            AbstractMailTemplate template = (AbstractMailTemplate) Class.forName(templateName).newInstance();
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