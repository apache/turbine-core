package org.apache.turbine.util;

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

/**
 * CookieParser is an interface to a utility to to get and set values
 * of Cookies on the Client Browser. You can use CookieParser to convert
 * Cookie values to various types or to set Bean values with setParameters().
 * Servlet Spec for more information on Cookies.
 * <p>
 * Use set() or unset() to Create or Destroy Cookies.
 * <p>
 * NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * <pre>
 * CookieParser cp = data.getCookies();
 * cp.add("ERROR",1);
 * cp.add("eRrOr",2);
 * int result = cp.getInt("ERROR");
 * </pre>
 *
 * In the above example, result is 2.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @version $Id$
 * @deprecated Use org.apache.turbine.util.parser.CookieParser instead.
 */
public interface CookieParser
        extends org.apache.turbine.util.parser.CookieParser
{
}
