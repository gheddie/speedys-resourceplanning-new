package de.trispeedys.resourceplanning.dto;

public class ManualAssignmentDTO
{
    private String taskId;
    
    private String helperName;

    private String wish;
    
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
    
    public String getWish()
    {
        return wish;
    }

    public void setWish(String wish)
    {
        this.wish = wish;
    }
}