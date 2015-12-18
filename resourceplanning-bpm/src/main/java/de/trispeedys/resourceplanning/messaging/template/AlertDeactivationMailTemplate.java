package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class AlertDeactivationMailTemplate extends AbstractMailTemplate
{
    public AlertDeactivationMailTemplate()
    {
        this(null, null, null);
    }

    public AlertDeactivationMailTemplate(Helper helper, Event event, Position position)
    {
        super(helper, event, position);
    }

    public String constructBody()
    {
        return new HtmlGenerator(true).withParagraph(AppConfiguration.getInstance().getText(this, "adminSulation"))
                .withParagraph(
                        AppConfiguration.getInstance().getText(this, "body", getHelper().getLastName(),
                                getHelper().getFirstName()))
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