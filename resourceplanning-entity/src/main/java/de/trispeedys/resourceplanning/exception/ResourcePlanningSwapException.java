package de.trispeedys.resourceplanning.exception;

public class ResourcePlanningSwapException extends ResourcePlanningException
{
    private static final long serialVersionUID = 2172910676953853161L;

    public ResourcePlanningSwapException(String message)
    {
        super(message);
    }

    public ResourcePlanningSwapException(String message, Throwable t)
    {
        super(message, t);
    }
}
