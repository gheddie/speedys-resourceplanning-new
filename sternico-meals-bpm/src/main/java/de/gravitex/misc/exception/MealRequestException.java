package de.gravitex.misc.exception;

public class MealRequestException extends RuntimeException
{
    private static final long serialVersionUID = -2354257088585288309L;
    
    public MealRequestException(String message, Throwable t)
    {
        super(message, t);
    }

    public MealRequestException(String message)
    {
        super(message);
    }
}