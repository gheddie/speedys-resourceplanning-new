package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;

public class RequestSwapMailTemplate extends HelperInteractionMailTemplate
{
    public RequestSwapMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }

    protected String getJspReceiverName()
    {
        return null;
    }

    public String constructBody()
    {
        return "body";
    }

    public String constructSubject()
    {
        return "subject";
    }

    @Override
    public MessagingType getMessagingType()
    {
        return null;
    }
}
