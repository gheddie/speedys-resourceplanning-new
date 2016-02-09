package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public abstract class SwapMailTemplate extends AbstractMailTemplate
{
    private HelperAssignment sourceAssignment;

    public SwapMailTemplate(Event event, HelperAssignment sourceAssignment)
    {
        super(event);        
        this.sourceAssignment = sourceAssignment;
    }
    
    public HelperAssignment getSourceAssignment()
    {
        return sourceAssignment;
    }
    
}