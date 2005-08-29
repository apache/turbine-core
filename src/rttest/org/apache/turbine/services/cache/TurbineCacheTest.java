package org.apache.turbine.services.cache;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;
import org.apache.turbine.services.cache.Refreshable;
import org.apache.turbine.services.cache.RefreshableCachedObject;
import org.apache.turbine.Turbine;

/**
 * TurbineCacheTest
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class TurbineCacheTest extends ServletTestCase {

    private Turbine turbine = null;
    private static final String cacheKey = new String("CacheKey");
    private static final String cacheKey_2 = new String("CacheKey_2");
    private static final long TURBINE_CACHE_REFRESH = 5000; // in millis
    private static final long TEST_EXPIRETIME = TURBINE_CACHE_REFRESH + 1000;
    private static final long TEST_TIMETOLIVE = TEST_EXPIRETIME * 5;
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TurbineCacheTest( String name )
    {
        super( name );
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { TurbineCacheTest.class.getName() });
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite( TurbineCacheTest.class );
    }

    /**
     * This setup will be running server side.  We startup Turbine and
     * get our test port from the properties.  This gets run before
     * each testXXX test.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        config.setInitParameter("properties",
                "/WEB-INF/conf/TurbineCacheTest.properties");
        turbine = new Turbine();
        turbine.init(config);
    }

    /**
     * After each testXXX test runs, shut down the Turbine servlet.
     */
    protected void tearDown()
        throws Exception
    {
        turbine.destroy();
        super.tearDown();
    }

    /**
     * Simple test that verify an object can be created and deleted.
     * @throws Exception
     */
    public void testSimpleAddGetCacheObject() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject1 = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create object
        cacheObject1 = new CachedObject(testString);
        assertNotNull( "Failed to create a cachable object 1", cacheObject1);

        // Add object to cache
        globalCache.addObject(cacheKey, cacheObject1);

        // Get object from cache
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull( "Did not retrieved a cached object 1", retrievedObject);
        assertTrue( "Did not retrieved a correct, expected cached object 1", retrievedObject == cacheObject1);

        // Remove object from cache
        globalCache.removeObject(cacheKey);

        // Verify object removed from cache
        retrievedObject = null;
        cacheObject1 = null;
        try
        {
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull( "Retrieved the deleted cached object 1 and did not get expected ObjectExpiredException", retrievedObject);
            assertNotNull( "Did not get expected ObjectExpiredException retrieving a deleted object", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull( "Retrieved the deleted cached object 1, but caught expected ObjectExpiredException exception", retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Remove object from cache that does NOT exist in the cache
        globalCache.removeObject(cacheKey);
    }

    /**
     * Simple test that adds, retrieves, and deletes 2 object.
     *
     * @throws Exception
     */
    public void test2ObjectAddGetCachedObject() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject1 = null;
        CachedObject cacheObject2 = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create and add Object #1
        cacheObject1 = new CachedObject(testString);
        assertNotNull( "Failed to create a cachable object 1", cacheObject1);
        globalCache.addObject(cacheKey, cacheObject1);
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull( "Did not retrieved a cached object 1", retrievedObject);
        assertEquals( "Did not retrieved correct cached object", cacheObject1, retrievedObject);

        // Create and add Object #2
        cacheObject2 = new CachedObject(testString);
        assertNotNull( "Failed to create a cachable object 2", cacheObject2);
        globalCache.addObject(cacheKey_2, cacheObject2);
        retrievedObject = globalCache.getObject(cacheKey_2);
        assertNotNull( "Did not retrieved a cached object 2", retrievedObject);
        assertEquals( "Did not retrieved correct cached object 2", cacheObject2, retrievedObject);

        // Get object #1
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull( "Did not retrieved a cached object 1. Attempt #2", retrievedObject);
        assertEquals( "Did not retrieved correct cached object 1. Attempt #2", cacheObject1, retrievedObject);

        // Get object #1
        retrievedObject = globalCache.getObject(cacheKey);
        assertNotNull( "Did not retrieved a cached object 1. Attempt #3", retrievedObject);
        assertEquals( "Did not retrieved correct cached object 1. Attempt #3", cacheObject1, retrievedObject);

        // Get object #2
        retrievedObject = globalCache.getObject(cacheKey_2);
        assertNotNull( "Did not retrieved a cached object 2. Attempt #2", retrievedObject);
        assertEquals( "Did not retrieved correct cached object 2 Attempt #2", cacheObject2, retrievedObject);

        // Remove objects
        globalCache.removeObject(cacheKey);
        globalCache.removeObject(cacheKey_2);
    }

    /**
     * Verify that an object will throw the ObjectExpiredException
     * when it now longer exists in cache.
     *
     * @throws Exception
     */
    public void testObjectExpiration() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create and add Object that expires in 1000 millis (1 second)
        cacheObject = new CachedObject(testString, 1000);
        assertNotNull( "Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);

        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull( "Did not retrieved a cached object", retrievedObject);
            assertEquals( "Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertTrue( "Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Sleep 1500 Millis (1.5 seconds)
        Thread.sleep(1500);

        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull( "Retrieved the expired cached object  and did not get expected ObjectExpiredException", retrievedObject);
            assertNotNull( "Did not get expected ObjectExpiredException retrieving an expired object", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull( "Retrieved the expired cached object, but caught expected ObjectExpiredException exception", retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Remove objects
        globalCache.removeObject(cacheKey);
    }

    /**
     * Verify the all object will be flushed from the cache.
     *
     * This test can take server minutes.
     *
     * @throws Exception
     */
    public void testCacheFlush() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        CachedObject cacheObject = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create and add Object that expires in 1 turbine Refresh + 1 millis
        cacheObject = new CachedObject(testString, (TURBINE_CACHE_REFRESH*5) + 1);
        assertNotNull( "Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);

        // 1 Refresh
        Thread.sleep(TURBINE_CACHE_REFRESH + 1);
        assertTrue("No object in cache before flush", (0 < globalCache.getNumberOfObjects()));

        // Flush Cache
        globalCache.flushCache();

        // Wait 15 seconds, 3 Refresh
        Thread.sleep((TURBINE_CACHE_REFRESH * 2) + 1);
        assertEquals("After refresh", 0, globalCache.getNumberOfObjects());

        // Remove objects
        globalCache.removeObject(cacheKey);
    }

    /**
     * Verify the Cache count is correct.
     * @throws Exception
     */
    public void testObjectCount() throws Exception
    {
        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );
        assertNotNull("Could not retrive cache service.", globalCache);

        // Create and add Object that expires in 1.5 turbine Refresh
        long expireTime = TURBINE_CACHE_REFRESH + TURBINE_CACHE_REFRESH/2;
        CachedObject cacheObject = new CachedObject("This is a test", expireTime);
        assertNotNull( "Failed to create a cachable object", cacheObject);

        globalCache.addObject(cacheKey, cacheObject);
        assertEquals("After adding 1 Object", 1, globalCache.getNumberOfObjects());

        // Wait until we're passed 1 refresh, but not half way.
        Thread.sleep(TURBINE_CACHE_REFRESH + TURBINE_CACHE_REFRESH/3);
        assertEquals("After one refresh", 1, globalCache.getNumberOfObjects());

        // Wait until we're passed 2 more refreshes
        Thread.sleep((TURBINE_CACHE_REFRESH * 2) + TURBINE_CACHE_REFRESH/3);
        assertEquals("After three refreshes", 0, globalCache.getNumberOfObjects());
    }

    /**
     * Verfy a refreshable object will refreshed in the following cases:
     * o The object is retrieved via getObject an it is stale.
     * o The object is determied to be stale during a cache
     *   refresh
     *
     * This test can take serveral minutes.
     *
     * @throws Exception
     */
    public void testRefreshableObject() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        RefreshableCachedObject cacheObject = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create and add Object that expires in TEST_EXPIRETIME millis.
        cacheObject = new RefreshableCachedObject(new RefreshableObject(), TEST_EXPIRETIME);
        assertNotNull( "Failed to create a cachable object", cacheObject);
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);

        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull( "Did not retrieved a cached object", retrievedObject);
            assertEquals( "Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertTrue( "Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Wait 1 Turbine cache refresh + 1 second.
        Thread.sleep(TEST_EXPIRETIME + 1000);

        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull( "Did not retrieved a cached object, after sleep", retrievedObject);
            assertNotNull( "Cached object has no contents, after sleep.", ((RefreshableCachedObject)retrievedObject).getContents());
            assertTrue( "Object did not refresh.", ( ((RefreshableObject)((RefreshableCachedObject)retrievedObject).getContents()).getRefreshCount() > 0));
        }
        catch (ObjectExpiredException e)
        {
            assertTrue( "Received unexpected ObjectExpiredException exception "
            + "when retrieving refreshable object after ( "
            + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }

        // See if object will expires (testing every second for 100 seconds.  It sould not!
        for (int i=0; i<100; i++)
        {
            Thread.sleep(1000); // Sleep 0.5 seconds

            // Try to get expired object
            try
            {
                retrievedObject = null;
                retrievedObject = globalCache.getObject(cacheKey);
                assertNotNull( "Did not retrieved a cached object, after sleep", retrievedObject);
                assertNotNull( "Cached object has no contents, after sleep.", ((RefreshableCachedObject)retrievedObject).getContents());
                assertTrue( "Object did not refresh.", ( ((RefreshableObject)((RefreshableCachedObject)retrievedObject).getContents()).getRefreshCount() > 0));
            }
            catch (ObjectExpiredException e)
            {
                assertTrue( "Received unexpected ObjectExpiredException exception "
                + "when retrieving refreshable object after ( "
                + (System.currentTimeMillis() - addTime) + " millis)", false);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        // Remove objects
        globalCache.removeObject(cacheKey);
    }

    /**
     * Verify a cached object will be delete after it has been
     * untouched beyond it's TimeToLive.
     *
     * This test can take serveral minutes.
     *
     * @throws Exception
     */
    public void testRefreshableTimeToLive() throws Exception
    {
        String testString = new String( "This is a test");
        Object retrievedObject = null;
        RefreshableCachedObject cacheObject = null;

        GlobalCacheService globalCache = (GlobalCacheService)TurbineServices
        .getInstance()
        .getService( GlobalCacheService.SERVICE_NAME );

        // Create and add Object that expires in TEST_EXPIRETIME millis.
        cacheObject = new RefreshableCachedObject(new RefreshableObject(), TEST_EXPIRETIME);
        assertNotNull( "Failed to create a cachable object", cacheObject);
        cacheObject.setTTL(TEST_TIMETOLIVE);

        // Verify TimeToLive was set
        assertEquals( "Returned TimeToLive", TEST_TIMETOLIVE, cacheObject.getTTL());

        // Add object to Cache
        long addTime = System.currentTimeMillis();
        globalCache.addObject(cacheKey, cacheObject);

        // Try to get un-expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull( "Did not retrieved a cached object", retrievedObject);
            assertEquals( "Did not retrieved correct cached object", cacheObject, retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertTrue( "Object expired early ( " + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Wait long enough to allow object to expire, but do not exceed TTL
        Thread.sleep(TEST_TIMETOLIVE - 2000);

        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNotNull( "Did not retrieved a cached object, after sleep", retrievedObject);
            assertNotNull( "Cached object has no contents, after sleep.", ((RefreshableCachedObject)retrievedObject).getContents());
            assertTrue( "Object did not refresh.", ( ((RefreshableObject)((RefreshableCachedObject)retrievedObject).getContents()).getRefreshCount() > 0));
        }
        catch (ObjectExpiredException e)
        {
            assertTrue( "Received unexpected ObjectExpiredException exception "
            + "when retrieving refreshable object after ( "
            + (System.currentTimeMillis() - addTime) + " millis)", false);
        }
        catch (Exception e)
        {
            throw e;
        }

        // Wait long enough to allow object to expire and exceed TTL
        Thread.sleep(TEST_TIMETOLIVE +5000);

        // Try to get expired object
        try
        {
            retrievedObject = null;
            retrievedObject = globalCache.getObject(cacheKey);
            assertNull( "Retrieved a cached object, after exceeding TimeToLive", retrievedObject);
        }
        catch (ObjectExpiredException e)
        {
            assertNull( "Retrieved the expired cached object, but caught expected ObjectExpiredException exception", retrievedObject);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    /**
     * Simple object that can be refreshed
     */
    class RefreshableObject implements Refreshable
    {

        private int refreshCount = 0;

        /**
         * Increment the refresh counter
         */
        public void refresh()
        {
            this.refreshCount++;
        }

        /**
         * Reutrn the number of time this object has been refreshed
         *
         * @return Number of times refresh() has been called
         */
        public int getRefreshCount()
        {
            return this.refreshCount;
        }
    }
}
