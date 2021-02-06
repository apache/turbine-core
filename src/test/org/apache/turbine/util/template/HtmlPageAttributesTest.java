package org.apache.turbine.util.template;

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


import static org.junit.jupiter.api.Assertions.*;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing of the HtmlPageAttributes class
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class HtmlPageAttributesTest extends BaseTestCase
{

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_KEY )
    private String defaultHtmlDoctypeRootElement = TurbineConstants.DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_DEFAULT;

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_IDENTIFIER_KEY )
    private String defaultHtmlDoctypeIdentifier;

    @TurbineConfiguration( TurbineConstants.DEFAULT_HTML_DOCTYPE_URI_KEY )
    private String defaultHtmlDoctypeUri;

    private static TurbineConfig tc = null;

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

    @Test public void testBuildDoctype() throws TurbineException
    {
        HtmlPageAttributes page = new HtmlPageAttributes();
        assertEquals("<!DOCTYPE html>", page.getDoctype("html", null, null));
        assertEquals("<!DOCTYPE html>", page.getDoctype("html", "", ""));
        assertEquals("<!DOCTYPE html SYSTEM \"bla\">", page.getDoctype("html", "", "bla"));

       // by default empty in HTML 5
        assertEquals("<!DOCTYPE HTML>",
                page.getDoctype(TurbineConstants.DEFAULT_HTML_DOCTYPE_ROOT_ELEMENT_DEFAULT,
                        TurbineConstants.DEFAULT_HTML_DOCTYPE_IDENTIFIER_DEFAULT,
                        TurbineConstants.DEFAULT_HTML_DOCTYPE_URI_DEFAULT));
        // test old style locally
        AnnotationProcessor.process(this);
        assertEquals("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" "
                + "\"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">",
                page.getDoctype(defaultHtmlDoctypeRootElement,
                                defaultHtmlDoctypeIdentifier,
                                defaultHtmlDoctypeUri));

        // HTML 5 is the default
        assertEquals("<!DOCTYPE HTML>", page.getDefaultDoctype());
    }
}
