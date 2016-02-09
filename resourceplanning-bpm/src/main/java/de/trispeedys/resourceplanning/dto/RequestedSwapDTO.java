package de.trispeedys.resourceplanning.dto;

public class RequestedSwapDTO
{
    private String sourcePosition;
    
    private String targetPosition;
    
    public String getSourcePosition()
    {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition)
    {
        this.sourcePosition = sourcePosition;
    }
    
    public String getTargetPosition()
    {
        return targetPosition;
    }

    public void setTargetPosition(String targetPosition)
    {
        this.targetPosition = targetPosition;
    }
}