package org.apache.turbine.services.intake;

import org.apache.turbine.util.TurbineException;

/**
 * Base exception thrown by the Intake service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class IntakeException extends TurbineException
{
    /**
     * Constructs a new <code>TurbineException</code> without specified
     * detail message.
     */
    public IntakeException()
    {
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message.
     *
     * @param msg The error message.
     */
    public IntakeException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new <code>TurbineException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg    The error message.
     * @param nested The exception or error that caused this exception
     *               to be thrown.
     */
    public IntakeException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

}
