package org.apache.turbine.services.security;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.factory.FactoryService;
import org.apache.turbine.services.factory.TurbineFactoryService;
import org.apache.turbine.services.security.db.DBSecurityService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.TurbineAccessControlList;

public class AccessControlListTest
    extends BaseTestCase
{
    private static final String PREFIX = "services." +
        SecurityService.SERVICE_NAME + '.';

    public AccessControlListTest( String name )
            throws Exception
    {
        super(name);
    }

    public void testSelection()
    {
         try
        {
            doit();
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
   }

    public void doit()
        throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();

        cfg.setProperty(PREFIX + "classname",
                        DBSecurityService.class.getName());

        cfg.setProperty(PREFIX + "acl.class",
                        TurbineAccessControlList.class.getName());

        // We must run init!
        cfg.setProperty(PREFIX+"earlyInit", "true");

        /* Ugh */

        cfg.setProperty("services." + FactoryService.SERVICE_NAME + ".classname",
                        TurbineFactoryService.class.getName());

        serviceManager.setConfiguration(cfg);

        serviceManager.init();

        Class aclClass = TurbineSecurity.getService().getAclClass();

        if(!aclClass.getName().equals(TurbineAccessControlList.class.getName()))
        {
            fail("ACL Class is " + aclClass.getName()
                 + ", expected was " + TurbineAccessControlList.class.getName());
        }

        Map roles = new HashMap();
        Map permissions = new HashMap();

        AccessControlList aclTest =
          TurbineSecurity.getService().getAclInstance(roles, permissions);

        if(aclTest == null)
        {
          fail("Security Service failed to deliver a " + aclClass.getName()
               + " Object");
        }
    }
}
