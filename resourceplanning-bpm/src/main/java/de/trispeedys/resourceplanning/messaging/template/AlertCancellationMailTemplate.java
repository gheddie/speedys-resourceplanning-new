package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class AlertCancellationMailTemplate extends AbstractMailTemplate
{
    public AlertCancellationMailTemplate()
    {
        this(null, null, null);
    }

    public AlertCancellationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(configuration.getText(this, "adminSalutation"))
                .withParagraph(
                        configuration.getText(this, "body", getHelper().getLastName(), getHelper().getFirstName(),
                                getPosition().getDescription()))
                .render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject", getEvent().getDescription());
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.ALERT_BOOKING_CANCELLED;
    }
}