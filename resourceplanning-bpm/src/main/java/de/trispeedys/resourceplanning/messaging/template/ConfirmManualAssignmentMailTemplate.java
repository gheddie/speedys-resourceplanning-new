package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

@SuppressWarnings("rawtypes")
public class ConfirmManualAssignmentMailTemplate extends AbstractMailTemplate
{
    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(AppConfiguration.getInstance().getText(this, "body", getEvent().getDescription()))
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject");
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.MANUAL_ASSIGNMENT_CONFIRMATION;
    }
}