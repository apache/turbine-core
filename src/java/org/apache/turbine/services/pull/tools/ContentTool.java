package org.apache.turbine.services.pull.tools;


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



import org.apache.commons.configuration.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.DataURI;

/**
 * Terribly simple tool to translate URIs into Turbine Links.
 * Equivalent to URIUtils.getAbsoluteLink() in a pull tool.
 *
 * <p>
 * If you're missing any routines from the 'old' $content tool concerning
 * path_info or query data, you did use the wrong tool then. You should've used
 * the TemplateLink tool which should be available as "$link" in your context.
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */

public class ContentTool
    implements ApplicationTool
{
    /** Prefix for Parameters for this tool */
    public static final String CONTENT_TOOL_PREFIX = "tool.content";

    /**
     * Should this tool add Container Encoding to the URIs returned?
     * True might cause trouble e.g. if you run with Apache HTTP Daemon / Tomcat Combo.
     *
     * Default is false (like Turbine 2.2)
     */
    public static final String CONTENT_TOOL_ENCODING_KEY = "want.encoding";

    /** Default Value for CONTENT_TOOL_ENCODING_KEY */
    public static final boolean CONTENT_TOOL_ENCODING_DEFAULT = false;

    /** Should this tool return relative URIs or absolute? Default: Absolute. */
    public static final String CONTENT_TOOL_RELATIVE_KEY = "want.relative";

    /** Default Value for CONTENT_TOOL_RELATIVE_KEY */
    public static final boolean CONTENT_TOOL_RELATIVE_DEFAULT = false;

    /** Do we want the container to encode the response? */
    boolean wantEncoding = false;

    /** Do we want a relative link? */
    boolean wantRelative = false;

    /** Caches a DataURI object which provides the translation routines */
    private DataURI dataURI = null;

    /**
     * C'tor
     */
    public ContentTool()
    {
        // empty
    }

    /*
     * ========================================================================
     *
     * Application Tool Interface
     *
     * ========================================================================
     *
     */

    /**
     * This will initialise a ContentTool object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        // we just blithely cast to RunData as if another object
        // or null is passed in we'll throw an appropriate runtime
        // exception.
        if (data instanceof PipelineData)
        {
            PipelineData pipelineData = (PipelineData) data;
            RunData runData = (RunData)pipelineData;
            dataURI = new DataURI(runData);
        }
        else
        {
            dataURI = new DataURI((RunData) data);

        }

        Configuration conf =
                Turbine.getConfiguration().subset(CONTENT_TOOL_PREFIX);

        if (conf != null)
        {
            wantRelative = conf.getBoolean(CONTENT_TOOL_RELATIVE_KEY,
                    CONTENT_TOOL_RELATIVE_DEFAULT);

            wantEncoding = conf.getBoolean(CONTENT_TOOL_ENCODING_KEY,
                    CONTENT_TOOL_ENCODING_DEFAULT);
        }

        if (!wantEncoding)
        {
            dataURI.clearResponse();
        }
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Returns the Turbine URI of a given Path
     *
     * @param path The path to translate
     *
     * @return Turbine translated absolute path
     */
    public String getURI(String path)
    {
        dataURI.setScriptName(path);

        return wantRelative ?
                dataURI.getRelativeLink() : dataURI.getAbsoluteLink();
    }

    /**
     * Returns the Turbine URI of a given Path. The
     * result is always an absolute path starting with
     * the server scheme (http/https).
     *
     * @param path The path to translate
     *
     * @return Turbine translated absolute path
     */
    public String getAbsoluteURI(String path)
    {
        dataURI.setScriptName(path);

        return dataURI.getAbsoluteLink();
    }

    /**
     * Returns the Turbine URI of a given Path. The
     * result is always relative to the context of
     * the application.
     *
     * @param path The path to translate
     *
     * @return Turbine translated absolute path
     */
    public String getRelativeURI(String path)
    {
        dataURI.setScriptName(path);

        return dataURI.getRelativeLink();
    }

}
