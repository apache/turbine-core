package org.apache.turbine.util.hibernate;

import org.apache.commons.lang.exception.NestableException;
/**
 * A general PersistenceException that can be thrown by 
 * Hibernate DAO classes.
 *
 */
public class PersistenceException extends NestableException
{
    //~ Constructors ===========================================================

    /**
     * Constructor for PersistenceException.
     */
    public PersistenceException()
    {
        super();
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param message
     */
    public PersistenceException(String message)
    {
        super(message);
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param message
     * @param cause
     */
    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for PersistenceException.
     *
     * @param cause
     */
    public PersistenceException(Throwable cause)
    {
        super(cause);
    }

}
