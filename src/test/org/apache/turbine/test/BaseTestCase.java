package org.apache.turbine.test;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;

import org.apache.log4j.PropertyConfigurator;

import org.apache.turbine.Turbine;

import junit.framework.TestCase;

/**
 * Base functionality to be extended by all Apache Turbine test cases.  Test
 * case implementations are used to automate testing via JUnit.
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class BaseTestCase
        extends TestCase
{
    File log4jFile = new File("conf/test/Log4j.properties");

    public BaseTestCase(String name)
            throws Exception
    {
        super(name);

        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
            p.setProperty(Turbine.APPLICATION_ROOT_KEY, new File(".").getAbsolutePath());
            PropertyConfigurator.configure(p);

        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Could not open Log4J configuration file "
                    + log4jFile);
        }

        //
        // Set up Commons Logging to use the Log4J Logging
        //
        System.getProperties().setProperty(LogFactory.class.getName(),
                Log4jFactory.class.getName());
    }
}

