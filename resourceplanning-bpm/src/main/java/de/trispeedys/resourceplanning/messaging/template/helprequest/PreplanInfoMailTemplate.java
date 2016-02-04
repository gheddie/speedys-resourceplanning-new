package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

@SuppressWarnings("rawtypes")
public class PreplanInfoMailTemplate extends RequestHelpMailTemplate
{
    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph(helperGreeting(getHelper()))
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