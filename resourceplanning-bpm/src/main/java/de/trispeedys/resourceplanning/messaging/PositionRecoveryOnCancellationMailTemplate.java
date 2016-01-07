package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;

public class PositionRecoveryOnCancellationMailTemplate extends AbstractMailTemplate
{
    public PositionRecoveryOnCancellationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(
                        AppConfiguration.getInstance().getText(this, "body", getPosition().getDescription(),
                                getPosition().getDomain().getName()))
                .withLink("http://www.nicht-lustig.de/", "...nicht lustig...")
                .withParagraph(sincerely())
                .render();
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject");
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.POS_RECOVERY_ON_CANCELLATION;
    }
}