package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class BookingConfirmationMailTemplate extends AbstractMailTemplate
{
    public BookingConfirmationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        String link =
                HelperInteraction.getBaseLink() + "/AssignmentCancellationReceiver.jsp?helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId();
        return new HtmlGenerator(true).withParagraph("Hallo " + getHelper().getFirstName() + "!")
                .withParagraph(
                        "Du wurdest erfolgreich der Position '" +
                                getPosition().getDescription() + "' im Bereich '" + getPosition().getDomain().getName() +
                                "' zugeordnet. Falls Dir etwas dazwischenkommen sollte, kannst du diese Buchung " + "mit dem untenstehenden Link stornieren:")
                .withLink(link, "Kündigen")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
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