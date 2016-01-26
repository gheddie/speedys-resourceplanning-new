package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

@SuppressWarnings("rawtypes")
public class PreplanInfoMailTemplate extends AbstractMailTemplate
{
    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(
                        AppConfiguration.getInstance().getText(this, "body", getPosition().getDescription(),
                                getPosition().getDomain().getName()))
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject");
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PREPLAN_INFO;
    }
}