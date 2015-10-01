package org.apache.turbine.services;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.util.Hashtable;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class BaseInitableBroker
        implements InitableBroker
{
    /** A repository of Initable instances. */
    protected Hashtable<String, Initable> initables = new Hashtable<String, Initable>();

    /**
     * Names of classes being early-initialized are pushed onto this
     * stack.  A name appearing twice indicates a circular dependency
     * chain.
     */
    protected Stack<String> stack = new Stack<String>();

    /** Logging */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * Default constructor of InitableBroker.
     *
     * This constructor does nothing. Your brokers should be
     * singletons, therefore their constructors should be
     * private. They should also have public YourBroker getInstance()
     * methods.
     */
    protected BaseInitableBroker()
    {
        // empty
    }

    /**
     * Performs early initialization of an Initable class.
     *
     * @param className The name of the class to be initialized.
     * @param data An Object to be used for initialization activities.
     * @exception InitializationException Initialization was not successful.
     */
    @Override
    public void initClass(String className, Object data)
            throws InitializationException
    {
        // make sure that only one thread calls this method recursively
        synchronized (stack)
        {
            int pos = stack.search(className);
            if (pos != -1)
            {
                StringBuilder msg = new StringBuilder().append(className)
                        .append(" couldn't be initialized because of circular dependency chain:\n");
                for (int i = pos; i > 0; i--)
                {
                    msg.append(stack.elementAt(stack.size() - i - 1) + "->");
                }
                msg.append(className).append('\n');

                throw new InitializationException(msg.toString());
            }
            try
            {
                stack.push(className);
                Initable instance = getInitableInstance(className);
                if (!instance.getInit())
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
     * <code>Initable</code>, and return it to its initial (uninitialized)
     * state.
     *
     * @param className The name of the class to be uninitialized.
     */
    @Override
    public void shutdownClass(String className)
    {
        try
        {
            Initable initable = getInitableInstance(className);
            if (initable.getInit())
            {
                initable.shutdown();
                ((BaseInitable) initable).setInit(false);
            }
        }
        catch (InstantiationException e)
        {
            // Shutdown of a nonexistent class was requested.
            // This does not hurt anything, so we log the error and continue.
            log.error("Shutdown of a nonexistent class " +
                    className + " was requested", e);
        }
    }

    /**
     * Provides an instance of Initable class ready to work.
     *
     * If the requested class couldn't be instantiated or initialized,
     * an InstantiationException will be thrown. You needn't handle
     * this exception in your code, since it indicates fatal
     * misconfiguration of the system.
     *
     * @param className The name of the Initable requested.
     * @return An instance of the requested Initable.
     * @exception InstantiationException if there was a problem
     * during instantiation or initialization of the Initable.
     */
    @Override
    public Initable getInitable(String className)
            throws InstantiationException
    {
        Initable initable;
        try
        {
            initable = getInitableInstance(className);
            if (!initable.getInit())
            {
                synchronized (initable.getClass())
                {
                    if (!initable.getInit())
                    {
                        initable.init();
                    }
                    if (!initable.getInit())
                    {
                        // this exception will be caught & rethrown by this
                        // very method. getInit() returning false indicates
                        // some initialization issue, which in turn prevents
                        // the InitableBroker from passing a working
                        // instance of the initable to the client.
                        throw new InitializationException(
                                "init() failed to initialize class "
                                + className);
                    }
                }
            }
            return initable;
        }
        catch (InitializationException e)
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
     * @exception InstantiationException if the requested class can't
     * be instantiated.
     */
    protected Initable getInitableInstance(String className)
            throws InstantiationException
    {
        Initable initable = initables.get(className);

        if (initable == null)
        {
            try
            {
                initable = (Initable) Class.forName(className).newInstance();
            }

                    // those two errors must be passed to the VM
            catch (ThreadDeath t)
            {
                throw t;
            }
            catch (OutOfMemoryError t)
            {
                throw t;
            }

            catch (Throwable t)
            {
                // Used to indicate error condition.
                String msg = null;

                if (t instanceof NoClassDefFoundError)
                {
                    msg = "A class referenced by " + className +
                            " is unavailable. Check your jars and classes.";
                }
                else if (t instanceof ClassNotFoundException)
                {
                    msg = "Class " + className +
                            " is unavailable. Check your jars and classes.";
                }
                else if (t instanceof ClassCastException)
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

}
