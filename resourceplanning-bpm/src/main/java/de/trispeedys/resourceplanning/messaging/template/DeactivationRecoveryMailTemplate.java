package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

public class DeactivationRecoveryMailTemplate extends AbstractMailTemplate
{
    public DeactivationRecoveryMailTemplate(Helper aHelper, Event aEvent)
    {
        super(aHelper, aEvent, null);
    }

    public String constructBody()
    {
        String link =
                getBaseLink() +
                        "/DeactivationRecoveryReceiver.jsp?helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(configuration.getText(this, "body", getEvent().getDescription()))
                .withLink(link, configuration.getText(this, "preventDeactivation"))
                .withLinebreak()
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        return "Nachfrage vor Deaktivierung";
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.DEACTIVATION_REQUEST;
    }
}