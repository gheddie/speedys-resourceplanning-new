package de.trispeedys.resourceplanning;

public class AddPosition
{
    private String description;
    
    private int minimalAge;

    public AddPosition(String aDescription, int aMinimalAge, String aHelperLastName, String aHelperFirstName)
    {
        super();
        this.description = aDescription;
        this.minimalAge = aMinimalAge;
    }
    
    public String getDescription()
    {
        return description;
    }

    public int getMinimalAge()
    {
        return this.minimalAge;
    }
}