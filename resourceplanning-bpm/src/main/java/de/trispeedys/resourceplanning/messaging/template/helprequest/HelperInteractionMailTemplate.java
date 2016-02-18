package de.trispeedys.resourceplanning.messaging.template.helprequest;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.RequestHelpMailTemplate;

public abstract class HelperInteractionMailTemplate extends RequestHelpMailTemplate
{
    protected static final String GENERIC_RECEIVER = "GenericRequestReceiver";
    
    public HelperInteractionMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }
    
    protected abstract String getJspReceiverName();
}