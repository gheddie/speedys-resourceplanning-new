package de.trispeedys.resourceplanning.util.exception;

public class ResourcePlanningException extends RuntimeException
{
    private static final long serialVersionUID = 2172910676953853161L;

    public ResourcePlanningException(String message)
    {
        super(message);
    }

    public ResourcePlanningException(String message, Throwable t)
    {
        super(message, t);
    }
}