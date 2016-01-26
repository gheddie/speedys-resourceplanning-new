package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class ConfirmPauseMailTemplate extends AbstractMailTemplate
{
    public ConfirmPauseMailTemplate(Helper aHelper)
    {
        super(aHelper, null, null);
    }

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
        return "Bestätigung Deiner Absage";
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PAUSE_CONFIRM;
    }
}