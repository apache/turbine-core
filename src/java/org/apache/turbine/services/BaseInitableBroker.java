package org.apache.turbine.services;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import org.apache.turbine.util.TurbineException;

/**
 * A generic implementation of <code>InitableBroker</code>.
 * Functionality provided by the broker includes:
 *
 * <ul>
 *
 * <li>Maintaining single instance of each <code>Initable</code> in
 * the system.</li>
 *
 * <li>Early initialization of <code>Initables</code> during system
 * startup.</li>
 *
 * <li>Late initialization of <code>Initables</code> before they are
 * used.</li>
 *
 * <li>Providing instances of <code>Initables</code> to requesting
 * parties.</li>
 *
 * <li>Maintaining dependencies between <code>Initables</code> during
 * early initalization phases, including circular dependencies
 * detection.</li>
 *
 * </ul>
 *
 * @author <a href="mailto:burton@apache.org">Kevin Burton</a>
 * @author <a href="mailto:krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class BaseInitableBroker
    implements InitableBroker
{
    /** A repository of Initable instances. */
    protected Hashtable initables = new Hashtable();

    /**
     * Names of classes being early-initialized are pushed onto this
     * stack.  A name appearing twice indicates a circular dependency
     * chain.
     */
    protected Stack stack = new Stack();

    /**
     * Default constructor of InitableBorker.
     *
     * This constructor does nothing. Your brokers should be
     * singletons, therefore their constructors should be
     * private. They should also have public YourBroker getInstance()
     * methods.
     */
    protected BaseInitableBroker()
    {
    }

    /**
     * Performs early initialization of an Initable class.
     *
     * @param className The name of the class to be initailized.
     * @param data An Object to be used for initialization activities.
     * @exception InitializationException Initialization was not successful.
     */
    public void initClass( String className,
                           Object data )
        throws InitializationException
    {
        // make sure that only one thread calls this method recursively
        synchronized(stack)
        {
            int pos = stack.search(className);
            if(pos != -1)
            {
                StringBuffer msg = new StringBuffer().append(className)
                    .append(" couldn't be initialized because of circular depency chain:\n");
                for(int i=pos; i>0; i--)
                {
                    msg.append((String)stack.elementAt(stack.size()-i-1)+"->");
                }
                msg.append(className).append('\n');

                throw new InitializationException(msg.toString());
            }
            try
            {
                stack.push(className);
                Initable instance = getInitableInstance(className);
                if(!instance.getInit())
                {
                    // this call might result in an indirect recursion
                    instance.init(data);
                }
            }
            finally
            {
                // Succeeded or not, make sure the name gets off the stack.
                stack.pop();
            }
        }
    }

    /**
     * Shuts down an <code>Initable</code>.
     *
     * This method is used to release resources allocated by an
     * <code>Initable</code>, and return it to its initial (uninitailized)
     * state.
     *
     * @param className The name of the class to be uninitialized.
     */
    public void shutdownClass( String className )
    {
        try
        {
            Initable initable = getInitableInstance(className);
            if(initable.getInit())
            {
                initable.shutdown();
                ((BaseInitable)initable).setInit(false);
            }
        }
        catch( InstantiationException e )
        {
            // Shutdown of a nonexistent class was requested.
            // This does not hurt anything, so we log the error and continue.
            error(new TurbineException("Shutdown of a nonexistent class " +
                    className + " was requested", e));
        }
    }

    /**
     * Provides an instance of Initable class ready to work.
     *
     * If the requested class couldn't be instatiated or initialized,
     * an InstantiationException will be thrown. You needn't handle
     * this exception in your code, since it indicates fatal
     * misconfigurtion of the system.
     *
     * @param className The name of the Initable requested.
     * @return An instance of the requested Initable.
     * @exception InstantiationException, if there was a problem
     * during instantiation or initialization of the Initable.
     */
    public Initable getInitable( String className )
        throws InstantiationException
    {
        Initable initable;
        try
        {
            initable = getInitableInstance(className);
            if(!initable.getInit())
            {
                synchronized(initable.getClass())
                {
                    if(!initable.getInit())
                    {
                        initable.init();
                    }
                    if(!initable.getInit())
                    {
                        // this exception will be caught & rethrown by this very method.
                        // getInit() returning false indicates some initialization issue,
                        // which in turn prevents the InitableBroker from passing a working
                        // instance of the initable to the client.
                        throw new InitializationException(
                            "init() failed to initialize class " + className);
                    }
                }
            }
            return initable;
        }
        catch( InitializationException e )
        {
            throw new InstantiationException("Class " + className +
                            " failed to initialize", e);
        }
    }

    /**
     * Retrieves an instance of an Initable from the repository.
     *
     * If the requested class is not present in the repository, it is
     * instantiated and passed a reference to the broker, saved and
     * then returned.
     *
     * @param className The name of the class to be instantiated.
     * @exception InstantiationException, if the requested class can't
     * be instantiated.
     */
    protected Initable getInitableInstance( String className )
        throws InstantiationException
    {
        Initable initable = (Initable)initables.get(className);

        if(initable == null)
        {
            try
            {
                initable = (Initable)Class.forName(className).newInstance();
            }

            // those two errors must be passed to the VM
            catch( ThreadDeath t )
            {
                throw t;
            }
            catch( OutOfMemoryError t )
            {
                throw t;
            }

            catch( Throwable t )
            {
                // Used to indicate error condition.
                String msg = null;

                if(t instanceof NoClassDefFoundError)
                {
                    msg = "A class referenced by " + className +
                        " is unavailable. Check your jars and classes.";
                }
                else if(t instanceof ClassNotFoundException)
                {
                    msg = "Class " + className +
                        " is unavailable. Check your jars and classes.";
                }
                else if(t instanceof ClassCastException)
                {
                    msg = "Class " + className +
                        " doesn't implement Initable.";
                }
                else
                {
                    msg = "Failed to instantiate " + className;
                }

                throw new InstantiationException(msg, t);
            }

            initable.setInitableBroker(this);
            initables.put(className, initable);
        }

        return initable;
    }

    /**
     * Output a diagnostic notice.
     *
     * This method is used by the service framework classes for producing
     * tracing mesages that might be useful for debugging (newline terminated).
     *
     * <p>The default implementation uses system error stream. When writing
     * your own, remeber to direct that message to the proper logging
     * mechanism.
     *
     * @param msg the message to print.
     */
    public void notice(String msg)
    {
        System.err.println('[' + new Date().toString() + "] " + msg);
    }

    /**
     * Output an error message.
     *
     * This method is used by the service framework classes for displaying
     * stacktraces of any exceptions that might be caught during processing.
     *
     * <p>The default implementation uses system error stream. When writing
     * your own, remeber to direct that message to the proper logging
     * mechanism.
     *
     * @param msg the message to print.
     */
    public void error(Throwable t)
    {
        t.printStackTrace(System.err);
    }
}
