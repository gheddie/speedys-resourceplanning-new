package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

public class ConfirmCancelForeverMailTemplate extends AbstractMailTemplate
{
    public String constructBody()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(configuration.getText(this, "body"))
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        // TODO translate
        return "Bestätigung Deiner permanenten Absage";
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.CANCEL_FOREVER_CONFIRMATION;
    }
}