package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.HtmlGenerator;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

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

    public MessagingType getMessagingType()
    {
        return MessagingType.ALERT_HELPER_DEACTIVATED;
    }
}