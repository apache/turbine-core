package org.apache.turbine.util.uri;

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

/**
 * This interface contains all the constants that are always needed when
 * working with URIs.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */

public interface URIConstants
{
    /** HTTP protocol. */
    String HTTP = "http";

    /** HTTPS protocol. */
    String HTTPS = "https";

    /** HTTP Default Port */
    int HTTP_PORT = 80;

    /** HTTPS Default Port */
    int HTTPS_PORT = 443;

    /** FTP Default Control Port */
    int FTP_PORT = 20;

    /** Path Info Data Marker */
    int PATH_INFO = 0;

    /** Query Data Marker */
    int QUERY_DATA = 1;

    /**
     * The part of the URI which separates the protocol indicator (i.e. the
     * scheme) from the rest of the URI.
     */
    String URI_SCHEME_SEPARATOR = "://";

    /** CGI parameter for action name */
    String CGI_ACTION_PARAM = "action";

    /** CGI parameter for screen name */
    String CGI_SCREEN_PARAM = "screen";

    /** CGI parameter for template name */
    String CGI_TEMPLATE_PARAM = "template";

    /** prefix for event names */
    String EVENT_PREFIX = "eventSubmit_";
}
