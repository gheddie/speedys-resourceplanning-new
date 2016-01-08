package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public abstract class HelperInteractionMailTemplate extends AbstractMailTemplate
{
    public HelperInteractionMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super(aHelper, aEvent, aPosition);
    }
    
    protected abstract String getJspReceiverName();
}