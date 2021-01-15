package org.apache.turbine.services.urlmapper;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.TemplateURI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TurbineURLMapperServiceTest extends BaseTestCase
{
    private TurbineConfig tc = null;

    private URLMapperService urlMapper = null;

    @BeforeEach
    public void setUp() throws Exception
    {
        tc =
                new TurbineConfig(
                        ".",
                        "/conf/test/TurbineURLMapperServiceTest.properties");
        tc.initialize();

        urlMapper = (URLMapperService) TurbineServices.getInstance().getService(URLMapperService.SERVICE_NAME);
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    /**
     * Tests
     *
     * <code>scheme://bob/wow/damn2/bookId/123</code>
     * <code>scheme://bob/wow/book/123</code>
     * <p>
     * and
     *
     * <code>scheme://bob/wow/damn2/bookId/123/template/Book.vm?detail=1&detail=2&view=collapsed</code>
     * <code>scheme://bob/wow/book/123/1?view=collapsed</code>
     *
     * @throws Exception
     */
    @Test
    public void testMapToURL() throws Exception
    {
        assertNotNull(urlMapper);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        PipelineData pipelineData = getPipelineData(request, response, tc.getTurbine().getServletConfig());
        assertNotNull(pipelineData);

        TemplateURI uri = new TemplateURI(pipelineData.getRunData());
        uri.clearResponse(); // avoid encoding on mocked HTTPServletResponse
        uri.addPathInfo("bookId", 123);
        uri.setTemplate("Book.vm");
        uri.addQueryData("detail", 0);

        urlMapper.mapToURL(uri);
        assertEquals("/wow/book/123", uri.getRelativeLink());
        assertTrue(uri.getPathInfo().isEmpty());
        assertTrue(uri.getQueryData().isEmpty());

        uri = new TemplateURI(pipelineData.getRunData());
        uri.clearResponse(); // avoid encoding on mocked HTTPServletResponse
        uri.addPathInfo("bookId", 123);
        uri.setTemplate("Book.vm");
        uri.addQueryData("detail", 1);
        uri.addQueryData("detail", 2);
        uri.addQueryData("view", "collapsed");

        urlMapper.mapToURL(uri);
        assertEquals("/wow/book/123/1?view=collapsed", uri.getRelativeLink());
        assertTrue(uri.getPathInfo().isEmpty());
        assertEquals(1, uri.getQueryData().size());
    }

    /**
     * Tests
     *
     * <code>scheme:///app/book/123/4</code>
     * <code>scheme:///wow/damn2/detail/4/bookId/123</code>
     *
     * @throws Exception
     */
    @Test
    public void testMapFromURL() throws Exception
    {
        assertNotNull(urlMapper);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        PipelineData pipelineData = getPipelineData(request, response, tc.getTurbine().getServletConfig());
        assertNotNull(pipelineData);
        ParameterParser pp = pipelineData.get(Turbine.class, ParameterParser.class);
        assertNotNull(pp);
        assertTrue(pp.keySet().isEmpty());

        urlMapper.mapFromURL("/app/book/123/4", pp);

        assertEquals(3, pp.keySet().size());
        assertEquals(123, pp.getInt("bookId"));
        assertEquals("Book.vm", pp.getString("template"));
        assertEquals(4, pp.getInt("detail"));

        // double check
        TemplateURI uri = new TemplateURI(pipelineData.getRunData());
        uri.clearResponse();
        uri.addPathInfo(pp);
        assertEquals("/wow/damn2/detail/4/bookId/123", uri.getRelativeLink());
        urlMapper.mapToURL(uri);
        assertEquals("/wow/book/123/4", uri.getRelativeLink());
    }


    @Tag("performance")
    @Test
    public void testPerformance() throws Exception
    {
        assertNotNull(urlMapper);
        int templateURIs = 5;
        List<AtomicLong> counterSum = new ArrayList<>();
        List<AtomicInteger> counters = new ArrayList<>();
        for (int i = 0; i < templateURIs; i++)
        {
            counters.add(i, new AtomicInteger(0));
            counterSum.add(i, new AtomicLong(0L));
        }
        int calls = 10_000; // above 1024, set max total of parser pool2 in fulcrum component configuration   ..
        boolean parallel = false;
        IntStream range = IntStream.range(0, calls);
        if (parallel)
        {
            range = range.parallel();
        }

        SplittableRandom sr = new SplittableRandom();

//        range
//        .peek(e -> System.out.println("current value: " + e))
//        .forEach( actionInt -> {
//        	runCheck(templateURIs, counterSum, counters, parallel, sr);
//        });

        Spliterator.OfInt spliterator1 = range.spliterator();
        Spliterator.OfInt spliterator2 = spliterator1.trySplit();

        System.out.println("s1 estimateSize: " + spliterator1.estimateSize());
        spliterator1.forEachRemaining((IntConsumer) i ->
        {
            runCheck(templateURIs, counterSum, counters, parallel, sr);
        });
        System.out.println("s2 estimateSize: " + spliterator2.estimateSize());
        spliterator2.forEachRemaining((IntConsumer) i ->
        {
            runCheck(templateURIs, counterSum, counters, parallel, sr);
        });

        for (int i = 0; i < counters.size() - 1; i++)
        {
            long time = counterSum.get(i).longValue() / 1_000_000;
            int count = counters.get(i).get();
            TemplateURI turi = getURI(i);
            String relativeLink = turi.getRelativeLink();
            callMapToUrl(turi);
            System.out.printf("time = %dms (%d calls),average time = %5.3fmics, uri=%s, map=%s%n", time, count,
                    count > 0 ? ((double) time * 1000 / count) : 0,
                    relativeLink, turi.getRelativeLink());
        }
        System.out.printf("total time = %dms (%d total calls) parallel:%s%n",
                counterSum.stream().mapToInt(i -> i.intValue()).sum() / 1_000_000,
                counters.stream().mapToInt(i -> i.intValue()).sum(),
                parallel
        );
    }

    private void runCheck(int templateURIs, List<AtomicLong> counterSum, List<AtomicInteger> counters, boolean parallel, SplittableRandom sr)
    {
        int randomNum = sr.nextInt(templateURIs);
        TemplateURI turi = getURI(randomNum);
        long time = System.nanoTime();
        try
        {
            callMapToUrl(turi);
        }
        finally
        {
            time = System.nanoTime() - time;
        	counterSum.get(randomNum).addAndGet(time);
        	counters.get(randomNum).incrementAndGet();
        }
    }

    /**
     * to get a fresh URI
     *
     * @param tnr
     * @return
     */
    private TemplateURI getURI(int tnr)
    {
        TemplateURI turi = null;
        switch (tnr)
        {
            case 0:
                turi = getURI1();
                break;
            case 1:
                turi = getURI2();
                break;
            case 2:
                turi = getURI3();
                break;
            case 3:
                turi = getURI4();
                break;
            case 4:
                turi = getURI5();
                break;
            default:
                break;
        }
        return turi;
    }

    private TemplateURI getURI1()
    {
        TemplateURI uri = new TemplateURI(getRunData());
        uri.clearResponse(); // avoid encoding on mocked HTTPServletResponse
        uri.addPathInfo("bookId", 123);
        uri.setTemplate("Book.vm");
        uri.addQueryData("detail", 0);
        return uri;
    }

    private TemplateURI getURI2()
    {
        TemplateURI uri2 = new TemplateURI(getRunData());
        uri2.clearResponse();
        uri2.addPathInfo("bookId", 123);
        uri2.setTemplate("Book.vm");
        uri2.addQueryData("detail", 1);
        uri2.addQueryData("detail", 2);
        uri2.addQueryData("view", "collapsed");
        return uri2;
    }

    private TemplateURI getURI3()
    {
        TemplateURI uri3 = new TemplateURI(getRunData());
        uri3.clearResponse();
        uri3.addPathInfo("id", 1234);
        uri3.addPathInfo("role", "guest");
        uri3.addPathInfo("language", "de");
        return uri3;
    }

    private TemplateURI getURI4()
    {
        TemplateURI uri4 = new TemplateURI(getRunData());
        uri4.clearResponse();
        uri4.addPathInfo("js_pane", "random-id-123-abc");
        uri4.addPathInfo("role", "anon");
        uri4.addPathInfo("media-type", "html");
        uri4.setTemplate("Registerone.vm");
        return uri4;
    }

    private TemplateURI getURI5()
    {
        TemplateURI uri5 = new TemplateURI(getRunData());
        uri5.clearResponse();
        uri5.addPathInfo("js_pane", "another-random-id-876-dfg");
        uri5.addPathInfo("role", "anon");
        uri5.addPathInfo("media-type", "html");
        uri5.addPathInfo("page", "Contact");
        return uri5;
    }

    private RunData getRunData()
    {
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        try
        {
            PipelineData pipelineData = getPipelineData(request, response, tc.getTurbine().getServletConfig());
            assertNotNull(pipelineData);
            return pipelineData.getRunData();
        }
        catch (Exception e)
        {
            fail();
        }
        return null;
    }

    private void callMapToUrl(TemplateURI uri)
    {
        urlMapper.mapToURL(uri);
        assertTrue(uri.getPathInfo().isEmpty(), "path is not empty:" + uri.getPathInfo());
    }
}
