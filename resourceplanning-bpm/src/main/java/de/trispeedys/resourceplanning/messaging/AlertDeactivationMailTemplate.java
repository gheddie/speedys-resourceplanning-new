package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class AlertDeactivationMailTemplate extends AbstractMailTemplate
{
    public AlertDeactivationMailTemplate(Helper helper, Event event, Position position)
    {
        super(helper, event, position);
    }

    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph("Hallo, Admin!!")
                .withParagraph(
                        "Helfer " +
                                getHelper().getLastName() + ", " + getHelper().getFirstName() + " wurde soeben deaktiviert!!")
                .render();
    }

    public String constructSubject()
    {
        return "Helfer-Deaktivierung";
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.ALERT_HELPER_DEACTIVATED;
    }
}