package org.apache.turbine.util;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.pool.PoolException;
import org.apache.fulcrum.pool.PoolService;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.services.rundata.DefaultTurbineRunData;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.rundata.TurbineRunData;
import org.apache.turbine.util.parser.DefaultCookieParser;
import org.apache.turbine.util.parser.DefaultParameterParser;

/**
 * Creates instances of RunData for use within Turbine or 3rd party
 * applications.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:burton@relativity.yi.org">Kevin A. Burton</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated This factory tries to be the RunData Service if no RunData Service is
 * configured. RunData Service is now mandatory for Turbine so use it directly without
 * this factory.
 */
public class RunDataFactory
{
    /** Logging */
    private static Log log = LogFactory.getLog(RunDataFactory.class);

    /**
     * A flag for the RunData Service.
     */
    private static boolean tryRunDataService = true;

    /**
     * The fulcrum pool service
     */
    private static PoolService pool;
    /**
     * Open way to get RunData information across Turbine..
     *
     * @param req An HttpServletRequest.
     * @param res An HttpServletResponse.
     * @param config A ServletConfig.
     * @throws TurbineException.
     */
    public static RunData getRunData(HttpServletRequest req,
                                     HttpServletResponse res,
                                     ServletConfig config)
            throws TurbineException,
                   IllegalArgumentException
    {
        // NOTE: getRunData( HttpServletRequest req,
        // HttpServletResponse res ) has been deprecated 3-3-2000.
        // Wait a couple months (before Turbine 1.0) and remove this
        // method.  Also don't allow null for req, res, or config as
        // these are now required by Turbine.  Uncomment the below as
        // this should include the necessary functionality when we are
        // ready.
        if (req == null ||
                res == null ||
                config == null)
        {
            throw new IllegalArgumentException(
                    "RunDataFactory fatal error: HttpServletRequest, " +
                    "HttpServletResponse or ServletConfig were null.");
        }

        // Create a new RunData object.  This object caches all the
        // information that is needed for the execution lifetime of a
        // single request.  A new RunData object is created for each
        // and every request and is passed to each and every module.
        // Since each thread has its own RunData object, it is not
        // necessary to perform syncronization for the data within
        // this object.
        // Try to retrieve the RunData implementation from the RunData Service.
        if (tryRunDataService)
        {
            try
            {
                
                return getRunDataService().getRunData(req, res, config);
            }
            catch (Exception x)
            {
                log.info("No Run Data Service available, not trying again!");
                tryRunDataService = false;
            }
        }

        // Failed, create a default implementation using the Pool Service.
        TurbineRunData data;
		try {
			data = (TurbineRunData) getPoolService().getInstance(DefaultTurbineRunData.class);
		} catch (ServiceException e) {
			throw new TurbineException(e);
		} catch (PoolException e) {
			throw new TurbineException(e);
		}

        // Cache some information that will be used elsewhere.
        data.setRequest(req);
        data.setResponse(res);

        // Let the implementation to create messages on demand.
        // data.setMessages(new FormMessages());

        // data.context = this.getServletContext();

        // Don't set this because if we want to output via
        // res.getOutputStream() then we will get an
        // IllegalStateException (already called getWriter()).  The
        // solution is to only do this if data.getOut() is called and
        // data.out is null. -jss

        // data.setOut(data.getResponse().getWriter());

        String contextPath = req.getContextPath();

        String scriptName = contextPath + data.getRequest().getServletPath();

        // Sets the default cookie parser.
        data.setCookieParser(new DefaultCookieParser());

        // Contains all of the GET/POST parameters.
        data.setParameterParser(new DefaultParameterParser());

        // Get the HttpSession object.
        data.setSession(data.getRequest().getSession(true));

        // Set the servlet configuration in RunData for use in loading
        // other servlets.
        data.setServletConfig(config);

        // Now set the ServerData.
        data.setServerData(new ServerData(data.getRequest().getServerName(),
                data.getRequest().getServerPort(),
                data.getRequest().getScheme(),
                scriptName,
                contextPath));
        return (RunData) data;
    }

    /**
     * Returns the used RunData object back to the factory for recycling.
     *
     * @param data the used RunData object.
     */
    public static void putRunData(RunData data)
    {
        // Try to return the RunData implementation to the RunData Service.
        if (tryRunDataService)
        {
            try
            {
                getRunDataService().putRunData(data);
                return;
            }
            catch (Exception x)
            {
            }
        }

        // Failed, use the Pool Service instead.
        try {
			getPoolService().putInstance(data);
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
    }
    
    /**
     * Static Helper method for looking up the RunDataService
     * @return A RunDataService
     */
    private static RunDataService getRunDataService(){
        return (RunDataService) TurbineServices
        .getInstance().getService(RunDataService.SERVICE_NAME);
    }
    
    private static PoolService getPoolService() throws ServiceException {
		if (pool == null) {
			AvalonComponentService acs = (AvalonComponentService) TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);
			pool = (PoolService)acs.lookup(PoolService.ROLE);
		}
    		return pool;
    }
}
