package org.apache.turbine.services.intake.validator;

import org.apache.turbine.services.intake.IntakeException;

/**
 * An Exception indidate an invalid field mask.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class InvalidMaskException
        extends IntakeException
{
    /**
     * Creates a new <code>InvalidMaskException</code> instance.
     *
     * @param message describing the reason validation failed.
     */
    public InvalidMaskException(String message)
    {
        super(message);
    }

    /**
     * Creates a new <code>InvalidMaskException</code> instance.
     *
     * @param cause Cause of the exception
     * @param message describing the reason validation failed.
     */
    public InvalidMaskException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
