package de.gravitex.hibernateadapter.core;

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