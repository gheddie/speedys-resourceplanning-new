package de.trispeedys.resourceplanning.dto;

public class ManualAssignmentDTO
{
    private String taskId;
    
    private String helperName;
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getHelperName()
    {
        return helperName;
    }

    public void setHelperName(String helperName)
    {
        this.helperName = helperName;        
    }
}