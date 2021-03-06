package de.trispeedys.resourceplanning.dto;

public class RequestedSwapDTO
{
    private String sourcePosition;
    
    private String targetPosition;

    private String sourceDomain;

    private String targetDomain;

    private String swapType;

    private String swapState;

    private String businessKey;
    
    private String sourceHelper;
    
    private String targetHelper;
    
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
    
    public String getSourceDomain()
    {
        return sourceDomain;
    }

    public void setSourceDomain(String sourceDomain)
    {
        this.sourceDomain = sourceDomain;
    }
    
    public String getTargetDomain()
    {
        return targetDomain;
    }

    public void setTargetDomain(String targetDomain)
    {
        this.targetDomain = targetDomain;
    }
    
    public String getSwapType()
    {
        return swapType;
    }

    public void setSwapType(String swapType)
    {
        this.swapType = swapType;
    }
    
    public String getSwapState()
    {
        return swapState;
    }

    public void setSwapState(String swapState)
    {
        this.swapState = swapState;
    }
    
    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }

    public String getSourceHelper()
    {
        return sourceHelper;
    }

    public void setSourceHelper(String sourceHelper)
    {
        this.sourceHelper = sourceHelper;
    }

    public String getTargetHelper()
    {
        return targetHelper;
    }

    public void setTargetHelper(String targetHelper)
    {
        this.targetHelper = targetHelper;
    }
}