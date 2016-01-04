package de.gravitex.hibernateadapter.core.exception;

public class HibernateAdapterException extends RuntimeException
{
    private static final long serialVersionUID = 3438668562886515616L;
    
    public HibernateAdapterException(String message, Throwable t)
    {
        super(message, t);
    }    
}