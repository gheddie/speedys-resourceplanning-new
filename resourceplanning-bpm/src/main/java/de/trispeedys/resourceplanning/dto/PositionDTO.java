package de.trispeedys.resourceplanning.dto;

public class PositionDTO
{
    private String description;
    
    private Long positionId;

    private String domain;

    private int minimalAge;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getPositionId()
    {
        return positionId;
    }

    public void setPositionId(Long positionId)
    {
        this.positionId = positionId;
    }
    
    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;        
    }
    
    public int getMinimalAge()
    {
        return minimalAge;
    }

    public void setMinimalAge(int minimalAge)
    {
        this.minimalAge = minimalAge;
    }
}