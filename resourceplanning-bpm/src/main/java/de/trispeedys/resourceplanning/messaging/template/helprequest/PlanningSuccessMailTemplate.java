package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;

@SuppressWarnings("rawtypes")
public class PlanningSuccessMailTemplate extends RequestHelpMailTemplate
{
    // TODO finish implementation!!
    
    public PlanningSuccessMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }
    
    public String constructBody()
    {
        return AppConfiguration.getInstance().getText(this, "body");
    }

    public String constructSubject()
    {
        return AppConfiguration.getInstance().getText(this, "subject", getEvent().getDescription());
    }

    public MessagingType getMessagingType()
    {
        return null;
    }
}