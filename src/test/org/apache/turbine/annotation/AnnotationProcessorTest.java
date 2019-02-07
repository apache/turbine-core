package org.apache.turbine.annotation;


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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.TurbineAccessControlList;
import org.apache.turbine.annotation.AnnotationProcessor.ConditionType;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests the various annotations
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class AnnotationProcessorTest
{
    private static TurbineConfig tc;

    @TurbineConfiguration
    private Configuration completeConfiguration = null;

    @TurbineConfiguration("serverdata.default")
    private Configuration serverdataDefaultConfiguration = null;

    @TurbineConfiguration("module.cache")
    private boolean moduleCache = true;

    @TurbineConfiguration("action.cache.size")
    private int actionCacheSize = 0;

    @TurbineConfiguration("template.homepage")
    private String templateHomepage;

    @TurbineConfiguration("module.packages")
    private List<String> modulePackages;

    @TurbineConfiguration("does.not.exist")
    private long notModified = 1;

    @TurbineLoader(Screen.class)
    private ScreenLoader screenLoader;

    @TurbineService
    private AssemblerBrokerService asb;

    @TurbineService
    private FactoryService factory;

    @BeforeAll
    public static void init() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @AfterAll
    public static void destroy()
        throws Exception
    {
        tc.dispose();
    }

    @BeforeEach
    public void setUpBefore() throws Exception
    {
        // do nothing
    }

    @Test
    public void testProcess() throws Exception
    {
        AnnotationProcessor.process(this);

        assertNotNull(completeConfiguration);
        assertFalse(completeConfiguration.getBoolean("module.cache", true));

        assertNotNull(serverdataDefaultConfiguration);
        assertEquals(80, serverdataDefaultConfiguration.getInt("serverPort"));

        assertFalse(moduleCache);
        assertEquals(20, actionCacheSize);
        assertEquals("Index.vm", templateHomepage);
        assertNotNull(modulePackages);
        assertEquals(3, modulePackages.size());
        assertEquals("org.apache.turbine.services.template.modules", modulePackages.get(1));
        assertEquals(1, notModified);

        assertNotNull(screenLoader);
        assertNotNull(asb);
        assertNotNull(factory);

    }

    @TurbineRequiredRole({"user","admin"})
    public void guardedMethoded() {
        // do nothing
    }

    @Test
    public void testRequiredRoleMethodProcess() throws Exception
    {
        RunData data = mock(RunData.class);
        TurbineAccessControlList acl = mock(TurbineAccessControlList.class);
        Role role = mock(Role.class);
        when(role.getName()).thenReturn( "user" );
        // Group group = mock(Group.class);
        when(acl.hasRole( role.getName() )).thenReturn( Boolean.TRUE );
        when(data.getACL()).thenReturn(acl );

        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals( "guardedMethoded" )) {
                assertTrue( AnnotationProcessor.isAuthorized( m, (TurbineAccessControlList)data.getACL(), ConditionType.ANY ));
                assertFalse( AnnotationProcessor.isAuthorized( m, (TurbineAccessControlList)data.getACL(), ConditionType.COMPOUND ));
            }
        }
    }

    @TurbineRequiredRole({"admin"})
    public void guardedMethodedAdmin() {
        // do nothing
    }

    @Test
    public void testRequiredRoleAdminMethodProcess() throws Exception
    {
        RunData data = mock(RunData.class);
        TurbineAccessControlList acl = mock(TurbineAccessControlList.class);
        Role role = mock(Role.class);
        when(role.getName()).thenReturn( "user" );
        // Group group = mock(Group.class);
        when(acl.hasRole( role.getName() )).thenReturn( Boolean.TRUE );
        when(data.getACL()).thenReturn(acl );

        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals( "guardedMethodedAdmin" )) {
                assertFalse( AnnotationProcessor.isAuthorized( m, (TurbineAccessControlList)data.getACL(), ConditionType.ANY ));
            }
        }
    }

    public void unguardedMethoded() {
        // do nothing
    }

    @Test
    public void testUnguardedMethodWProcessDefault() throws Exception
    {
        RunData data = mock(RunData.class);
        TurbineAccessControlList acl = mock(TurbineAccessControlList.class);
        Role role = mock(Role.class);
        when(role.getName()).thenReturn( "user" );
        // Group group = mock(Group.class);
        when(acl.hasRole( role.getName() )).thenReturn( Boolean.FALSE );
        when(data.getACL()).thenReturn(acl );

        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals( "unguardedMethoded" )) {
                // default is true, if not annotated
                assertTrue( AnnotationProcessor.isAuthorized( m, (TurbineAccessControlList)data.getACL(), ConditionType.ANY ));
            }
        }
    }

    //@Disabled("For performance tests only") 
    @Tag("performance") // ignore in surefire, activating seems to be still buggy ?
    @Test
    public void testProcessingPerformance() throws TurbineException
    {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++)
        {
            AnnotationProcessor.process(this);
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }
}
