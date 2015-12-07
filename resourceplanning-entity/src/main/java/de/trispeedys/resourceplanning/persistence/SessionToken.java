package de.trispeedys.resourceplanning.persistence;

public class SessionToken
{
    private String key;

    public SessionToken(String key)
    {
        super();
        this.key = key;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+key+"]";
    }
}