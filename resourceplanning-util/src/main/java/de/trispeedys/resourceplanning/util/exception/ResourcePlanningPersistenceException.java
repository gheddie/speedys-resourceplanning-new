package de.trispeedys.resourceplanning.util.exception;

public class ResourcePlanningPersistenceException extends ResourcePlanningException
{
    private static final long serialVersionUID = -8086725605667121055L;

    public ResourcePlanningPersistenceException(String message)
    {
        super(message);
    }

    public ResourcePlanningPersistenceException(String message, Throwable t)
    {
        super(message, t);
    }
}