package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

@SuppressWarnings("rawtypes")
public class ConfirmManualAssignmentMailTemplate extends AbstractMailTemplate
{
    public String constructBody()
    {
        // TODO render assignment wish in mail (not only on confirmation page)!!
        
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