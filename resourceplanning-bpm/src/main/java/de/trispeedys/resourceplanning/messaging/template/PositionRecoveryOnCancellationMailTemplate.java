package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class PositionRecoveryOnCancellationMailTemplate extends HelperInteractionMailTemplate
{
    public PositionRecoveryOnCancellationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        String link =
                getBaseLink() +
                        "/"+getJspReceiverName()+".jsp?helperId=" + getHelper().getId() + "&eventId=" +
                        getEvent().getId() + "&chosenPosition=" + getPosition().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(
                        configuration.getText(this, "body", getPosition().getDescription(), getPosition().getDomain()
                                .getName()))
                .withLink(link, configuration.getText(this, "recoveryLink"))
                .withLinebreak()
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
    
    protected String getJspReceiverName()
    {
        return "PositionRecoveryOnCancellationReceiver";
    }
}