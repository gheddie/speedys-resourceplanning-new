package de.trispeedys.resourceplanning.messaging.template;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.messaging.AbstractMailTemplate;

public abstract class HelperInteractionMailTemplate extends AbstractMailTemplate
{
    public HelperInteractionMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }
    
    protected abstract String getJspReceiverName();
}