package org.apache.turbine.services.avaloncomponent;

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
import java.net.URL;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.merlin.kernel.impl.DefaultKernel;
import org.apache.avalon.merlin.kernel.impl.DefaultKernelContext;
import org.apache.avalon.repository.impl.DefaultFileRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * An implementation of AvalonComponentService based on the Avalon 
 * Merlin 3.2 container.  Not ready for Prime Time yet.
 * 
 * @author <a mailto="peter@courcoux.biz">Peter Courcoux</a>
 * @author <a mailto="epugh@upstate.com">Eric Pugh</a>
 */
public class TurbineMerlinComponentService
    extends TurbineBaseService
    implements MerlinComponentService, Initializable, Disposable
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineMerlinComponentService.class);

    /** Merlin kernel */
    private DefaultKernel m_kernel = null;

    // -------------------------------------------------------------
    // Service initialization
    // -------------------------------------------------------------

    /**
	 * Load all configured components and initialize them. This is a zero parameter variant which
	 * queries the Turbine Servlet for its config.
	 * 
	 * @throws InitializationException Something went wrong in the init stage
	 */
    public void init() throws InitializationException
    {
        try
        {
            initialize();

            setInit(true);
        }
        catch (Exception e)
        {
            log.error("Exception caught initialising service: ", e);
            throw new InitializationException("init failed", e);
        }
    }

    /**
	 * Shuts the Component Service down, calls dispose on the components that implement this
	 * interface
	 *  
	 */
    public void shutdown()
    {
        dispose();
        setInit(false);
    }

    // -------------------------------------------------------------
    // Avalon lifecycle interfaces
    // -------------------------------------------------------------

    /**
	 * Initializes the container
	 * 
	 * @throws Exception generic exception
	 */
    public void initialize() throws Exception
    {
		org.apache.commons.configuration.Configuration conf 
					   = getConfiguration();
		
        String homePath = Turbine.getApplicationRoot();
        File home = new File(homePath);
        // Eric. The next thing to do is to decide how to create the blocks array.
        // Steve suggested listing all the block URL's in WEB.xml as initialisation params.
        // I think it needs to come from some local config. while we are keeping everything
        // in the webapp context.
        
		String blockPath = Turbine.getRealPath(
						conf.getString(BLOCK_CONFIG_KEY, BLOCK_CONFIG_PATH));
		File kernal = new File(Turbine.getRealPath(
								conf.getString(KERNAL_CONFIG_KEY, KERNAL_CONFIG_PATH)));
		//File userDir = new File(Turbine.getRealPath("target/test-classes"));
		if(!kernal.exists()){
		    throw new Exception("Kernal file " + kernal + " doesn't exist.  You must provide a " +
		    		"valid kernal file.");
		}
		
        URL block = new File(blockPath).toURL();
        URL[] blocks = new URL[1];
        blocks[0] = block;
        // Create the kernel context
        try
        {
                DefaultKernelContext context =
                    new DefaultKernelContext(
                        new DefaultFileRepository(new File(Turbine.getRealPath("merlin/dud"))),
            // system repo
        null, //new File( Turbine.getRealPath("merlin/repository")), // user repo directory
        null, //new File( Turbine.getRealPath("merlin/ext")), // ext directory
        home, // home
        kernal.toURL(), // kernel config
        blocks, // block urls
        null, // config override
        true, // server
        true, // info
        true // debug
    );
            m_kernel = new DefaultKernel(context);
            m_kernel.startup();
            log.info("kernel established");

            log.info("Application Root is " + homePath);
        }
        catch (Exception e)
        {
            final String message = "Turbine/Merlin problem.";
            final String error = ExceptionHelper.packException(message, e, true);
            log.info(error);
            System.out.println(error);
        }

    }

    /**
	 * Disposes of the container and releases resources
	 */
    public void dispose()
    {
        if (m_kernel != null)
        {
            m_kernel.shutdown();
            m_kernel = null;
        }
    }

    /**
	 * Returns an instance of the named component
	 * 
	 * @param roleName Name of the role the component fills.
	 * @return an instance of the named component
	 * @throws Exception generic exception
	 */
    public Object lookup(String path) throws Exception
    {
        Block root = m_kernel.getRootBlock();
        return root.locate(path).resolve();
    }

    /**
	 * Releases the component
	 * 
	 * @param source. The path to the handler for this component For example, if the object is a
	 *            java.sql.Connection object sourced from the "/turbine-merlin/datasource"
	 *            component, the call would be :- release("/turbine-merlin/datasource", conn);
	 * @param component the component to release
	 */
    public void release(Object component)
    {
        Block root = m_kernel.getRootBlock();
        root.release(component);
    }

}
