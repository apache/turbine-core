package org.apache.turbine.modules.actions.sessionvalidator;

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

import org.apache.turbine.modules.Action;

/**
 * The SessionValidator attempts to retrive the User object from the
 * Servlet API session that is associated with the request.  If the
 * data cannot be retrieved, it is handled here.  If the user has not
 * been marked as being logged into the system, the user is rejected
 * and the screen is set to the screen.homepage value in
 * TurbineResources.properties.
 *
 * <p>
 *
 * Other systems generally have a database table which stores this
 * information, but we take advantage of the Servlet API here to save
 * a hit to the database for each and every connection that a user
 * makes.
 *
 * <p>
 *
 * This action is special in that it should only be executed by the
 * Turbine servlet.
 *
 * @version $Id$
 */
public abstract class SessionValidator extends Action
{
}
