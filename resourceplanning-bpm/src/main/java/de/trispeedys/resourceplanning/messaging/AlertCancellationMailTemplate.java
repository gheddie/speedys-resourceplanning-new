package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class AlertCancellationMailTemplate extends AbstractMailTemplate
{
    public AlertCancellationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph("Hallo, Admin!!")
                .withParagraph(
                        "Helfer " +
                                getHelper().getLastName() + ", " + getHelper().getFirstName() + " (Position: " +
                                getPosition().getDescription() + ") hat leider abgesagt!!")
                .render();
    }

    public String constructSubject()
    {
        return "Helfer-Absage für den Wettkampf " + getEvent().getDescription();
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.ALERT_BOOKING_CANCELLED;
    }
}