package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class ConfirmPauseMailTemplate extends AbstractMailTemplate
{
    public ConfirmPauseMailTemplate(Helper aHelper)
    {
        super(aHelper, null, null);
    }

    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph("Hallo " + getHelper().getFirstName() + "!")
                .withParagraph("Schade, dass Du uns dieses Mal nicht helfen kannst. Bis zum nächsten Mal (?)!")
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String constructSubject()
    {
        return "Bestätigung Deiner Absage";
    }
    
    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.PAUSE_CONFIRM;
    }
}