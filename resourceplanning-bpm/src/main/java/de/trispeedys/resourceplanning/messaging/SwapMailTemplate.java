package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;

public abstract class SwapMailTemplate extends AbstractMailTemplate
{
    private HelperAssignment sourceAssignment;
    
    private HelperAssignment targetAssignment;

    public SwapMailTemplate(Event event, HelperAssignment sourceAssignment, HelperAssignment targetAssignment)
    {
        super(event);        
        this.sourceAssignment = sourceAssignment;
        this.targetAssignment = targetAssignment;
    }
    
    public HelperAssignment getSourceAssignment()
    {
        return sourceAssignment;
    }
    
    public HelperAssignment getTargetAssignment()
    {
        return targetAssignment;
    }
}