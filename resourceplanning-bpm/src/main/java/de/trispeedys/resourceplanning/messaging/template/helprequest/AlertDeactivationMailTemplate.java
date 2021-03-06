package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.GuidedEvent;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;
import de.trispeedys.resourceplanning.util.htmlgenerator.HtmlGenerator;

public class AlertDeactivationMailTemplate extends RequestHelpMailTemplate
{
    private boolean deactivationOnTimeout;

    public AlertDeactivationMailTemplate()
    {
        this(null, null, null);
    }

    public AlertDeactivationMailTemplate(Helper helper, GuidedEvent event, Position position)
    {
        super(helper, event, position);
    }

    public String constructBody()
    {
        String translationKey = null;
        if (deactivationOnTimeout)
        {
            translationKey = "bodyDeactivationOnTimeout";   
        }
        else
        {
            translationKey = "bodyManualDeactivation";
        }
        return new HtmlGenerator(true).withParagraph(AppConfiguration.getInstance().getText(this, "adminSulation"))
                .withParagraph(AppConfiguration.getInstance().getText(this, translationKey, getHelper().getLastName(), getHelper().getFirstName()))
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

    public void setDeactivationOnTimeout(boolean aDeactivationOnTimeout)
    {
        this.deactivationOnTimeout = aDeactivationOnTimeout;
    }
}