package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

public class BookingConfirmationMailTemplate extends AbstractMailTemplate
{
    public BookingConfirmationMailTemplate()
    {
        this(null, null, null);
    }

    public BookingConfirmationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        String link =
                getBaseLink() +
                        "/AssignmentCancellationReceiver.jsp?helperId=" + getHelper().getId() + "&eventId=" +
                        getEvent().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(configuration.getText(this, "body", getPosition().getDescription(),
                        getPosition().getDomain().getName()))
                .withLink(link, configuration.getText(this, "cancel"))
                .withLinebreak()
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        return "Buchungsbestätigung";
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.BOOKING_CONFIRMATION;
    }
}