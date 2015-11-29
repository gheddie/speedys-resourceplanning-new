package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class DeactivationRecoveryMailTemplate extends AbstractMailTemplate
{
    public DeactivationRecoveryMailTemplate(Helper aHelper, Event aEvent)
    {
        super(aHelper, aEvent, null);
    }

    public String constructBody()
    {
        String link =
                HelperInteraction.getBaseLink() +
                        "/DeactivationRecoveryReceiver.jsp?helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId();
        return new HtmlGenerator(true).withParagraph("Hallo " + getHelper().getFirstName() + "!")
                .withParagraph(
                        "Leider hast du auf keine unserer Nachfragen reagiert, ob du uns beim Event '"+getEvent().getDescription()+"' helfen kannst."
                                + " Nach Ablauf von 4 Wochen wird dein Helfer-Account deshalb deaktiviert. Mit dem Klicken auf den unten stehenden"
                                + " Link kannst du die Deaktivierung verhindern und wirst weiterhin als aktiver Helfer geführt:")
                .withLink(link, "Deaktivierung verhindern")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String constructSubject()
    {
        return "Nachfrage vor Deaktivierung";
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.DEACTIVATION_REQUEST;
    }
}