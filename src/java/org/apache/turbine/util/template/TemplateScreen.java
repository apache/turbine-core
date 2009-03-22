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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ecs.ConcreteElement;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.util.RunData;

/**
 * Returns output of a Screen module.  An instance of this is
 * placed in the Velocity context by the VelocityDirectLayout.  This
 * allows the screen to be executed only at rendering.
 * Here's how it's used in a template:
 *
 * <p>
 * <code>
 * $screen_placeholder
 * </code>
 * <p>
 * <code>
 * $screen_placeholder.setScreen("Test")
 * </code>
 * </p>
 *
 * @author <a href="raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class TemplateScreen
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplateScreen.class);

    /* The RunData object. */
    private RunData data;

    /* The name of the screen template. */
    private String screen;

    private ScreenLoader screenLoader;

    /**
     * Constructor
     *
     * @param data A Turbine RunData object.
     */
    public TemplateScreen(RunData data)
    {
        this.data = data;
        this.screen = data.getScreen();
        this.screenLoader = (ScreenLoader)TurbineAssemblerBroker.getLoader(Screen.NAME);
    }

    /**
     * Set the screen.
     *
     * @param screen A String with the name of the screen module
     * @return A TemplateScreen (self).
     */
    public TemplateScreen setScreen(String screen)
    {
        this.screen = screen;
        return this;
    }

    /**
     * Builds the output of the navigation template.
     *
     * @return A String.
     */
    public String toString()
    {
        String returnValue = "";

        try
        {
            ConcreteElement results = screenLoader.eval(data, this.screen);

            if (results != null)
            {
                returnValue = results.toString();
            }
        }
        catch (Exception e)
        {
            log.error(e);
        }

        return returnValue;
    }
}
