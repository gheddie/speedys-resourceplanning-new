package de.trispeedys.resourceplanning.dto;

public class ExecutionDTO
{
    private String helperFirstName;
    
    private String helperLastName;
    
    private String waitState;

    private String additionalInfo;
    
    public String getHelperFirstName()
    {
        return helperFirstName;
    }
    
    public void setHelperFirstName(String helperFirstName)
    {
        this.helperFirstName = helperFirstName;
    }
    
    public String getHelperLastName()
    {
        return helperLastName;
    }
    
    public void setHelperLastName(String helperLastName)
    {
        this.helperLastName = helperLastName;
    }

    public String getWaitState()
    {
        return waitState;
    }
    
    public void setWaitState(String waitState)
    {
        this.waitState = waitState;
    }
    
    public String getAdditionalInfo()
    {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + "[helperFirstName:"+helperFirstName+"|helperLastName:"+helperLastName+"|waitState:"+waitState+"]";
    }
}