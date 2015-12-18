package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

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
                HelperInteraction.getBaseLink() +
                        "/AssignmentCancellationReceiver.jsp?helperId=" + getHelper().getId() + "&eventId=" +
                        getEvent().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph("Hallo " + getHelper().getFirstName() + "!")
                .withParagraph(configuration.getText(this, "body", getPosition().getDescription(),
                        getPosition().getDomain().getName()))
                .withLinebreak()
                .withLink(link, configuration.getText(this, "cancel"))
                .withLinebreak()
                .withParagraph(configuration.getText(SPEEDYS_GREETING))
                .render();
    }

    public String constructSubject()
    {
        return "Buchungsbestätigung";
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.BOOKING_CONFIRMATION;
    }
}