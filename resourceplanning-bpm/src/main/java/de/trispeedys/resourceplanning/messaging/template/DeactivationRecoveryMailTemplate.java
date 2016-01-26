package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class DeactivationRecoveryMailTemplate extends HelperInteractionMailTemplate
{
    public DeactivationRecoveryMailTemplate(Helper aHelper, Event aEvent)
    {
        super(aHelper, aEvent, null);
    }

    public String constructBody()
    {
        String link =
                getBaseLink() +
                        "/"+getJspReceiverName()+".jsp?helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        return new HtmlGenerator(true).withParagraph(helperGreeting())
                .withParagraph(configuration.getText(this, "body", getEvent().getDescription()))
                .withLink(link, configuration.getText(this, "preventDeactivation"))
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
        return MessagingType.DEACTIVATION_REQUEST;
    }
    
    protected String getJspReceiverName()
    {
        return "DeactivationRecoveryReceiver";
    }
}