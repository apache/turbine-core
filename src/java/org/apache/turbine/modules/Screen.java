package org.apache.turbine.modules;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

// Turbine Utility Classes

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.Entities;
import org.apache.ecs.filter.CharacterFilter;
import org.apache.turbine.util.RunData;

/**
 * This is an interface that defines the Screen modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public abstract class Screen extends Assembler
{
    private static final CharacterFilter filter = htmlFilter();
    private static final CharacterFilter minFilter = htmlMinFilter();

    /**
     * A subclass must override this method to build itself.
     * Subclasses override this method to store the screen in RunData
     * or to write the screen to the output stream referenced in
     * RunData.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected abstract ConcreteElement doBuild(RunData data)
            throws Exception;

    /**
     * Subclasses can override this method to add additional
     * functionality.  This method is protected to force clients to
     * use ScreenLoader to build a Screen.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected ConcreteElement build(RunData data)
            throws Exception
    {
        return doBuild(data);
    }

    /**
     * If the Layout has not been defined by the Screen then set the
     * layout to be "DefaultLayout".  The Screen object can also
     * override this method to provide intelligent determination of
     * the Layout to execute.  You can also define that logic here as
     * well if you want it to apply on a global scale.  For example,
     * if you wanted to allow someone to define Layout "preferences"
     * where they could dynamicially change the Layout for the entire
     * site.  The information for the request is passed in with the
     * RunData object.
     *
     * @param data Turbine information.
     * @return A String with the Layout.
     */
    public String getLayout(RunData data)
    {
        return data.getLayout();
    }

    /**
     * Set the layout for a Screen.
     *
     * @param data Turbine information.
     * @param layout The layout name.
     */
    public void setLayout(RunData data, String layout)
    {
        data.setLayout(layout);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareText(String s)
    {
        return filter.process(s);
    }

    /**
     * This function can/should be used in any screen that will output
     * User entered text.  This will help prevent users from entering
     * html (<SCRIPT>) tags that will get executed by the browser.
     *
     * @param s The string to prepare.
     * @return A string with the input already prepared.
     */
    public static String prepareTextMinimum(String s)
    {
        return minFilter.process(s);
    }

    /**
     * These attributes are supposed to be the default, but they are
     * not, at least in ECS 1.2.  Include them all just to be safe.
     *
     * @return A CharacterFilter to do HTML filtering.
     */
    private static CharacterFilter htmlFilter()
    {
        CharacterFilter filter = new CharacterFilter();
        filter.addAttribute("\"", Entities.QUOT);
        filter.addAttribute("'", Entities.LSQUO);
        filter.addAttribute("&", Entities.AMP);
        filter.addAttribute("<", Entities.LT);
        filter.addAttribute(">", Entities.GT);
        return filter;
    }

    /*
     * We would like to filter user entered text that might be
     * dynamically added, using javascript for example.  But we do not
     * want to filter all the above chars, so we will just disallow
     * <.
     *
     * @return A CharacterFilter to do minimal HTML filtering.
     */
    private static CharacterFilter htmlMinFilter()
    {
        CharacterFilter filter = new CharacterFilter();
        filter.removeAttribute(">");
        filter.removeAttribute("\"");
        filter.removeAttribute("'");
        filter.removeAttribute("&");
        filter.addAttribute("<", Entities.LT);
        return filter;
    }
}
