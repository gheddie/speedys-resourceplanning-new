package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.configuration.AppConfigurationValues;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class BookingConfirmationMailTemplate extends HelperInteractionMailTemplate
{
    public BookingConfirmationMailTemplate()
    {
        this(null, null, null);
    }

    public BookingConfirmationMailTemplate(Helper aHelper, GuidedEvent aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    public String constructBody()
    {
        String link = getBaseLink() + "/" + getJspReceiverName() + ".jsp?helperId=" + getHelper().getId() + "&eventId=" + getEvent().getId() + "&positionId=" + getPosition().getId();
        AppConfiguration configuration = AppConfiguration.getInstance();
        HtmlGenerator generator =
                new HtmlGenerator(true).withParagraph(helperGreeting(getHelper()))
                        .withParagraph(configuration.getText(this, "body", getPosition().getDescription(), getPosition().getDomain().getName()))
                        .withLink(link, configuration.getText(this, "cancel"));
        
        // add helper info anyway...
        generator.withParagraph(configuration.getText(this, "helperInfoTeaser"));
        generator.withLink(configuration.getConfigurationValue(AppConfigurationValues.HELPER_CONFIRM_INFO), configuration.getText(this, "helperInfoLink"));
        
        /*
        if (!(getHelper().isInternal()))
        {
            // add helper info link
            generator.withParagraph(configuration.getText(this, "helperInfoTeaser"));
            generator.withLink(configuration.getConfigurationValue(AppConfigurationValues.HELPER_CONFIRM_INFO), configuration.getText(this, "helperInfoLink"));
        }
        */
        
        return generator.withParagraph(sincerely()).render();
    }

    public String constructSubject()
    {
        return "Buchungsbestätigung";
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.BOOKING_CONFIRMATION;
    }

    protected String getJspReceiverName()
    {
        return "AssignmentCancellationReceiver";
    }
}