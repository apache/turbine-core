package org.apache.turbine.util.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.ecs.html.Option;
import org.apache.ecs.html.Select;

/**
 * This class is for generating a SelectorBox. It is good when used
 * with WM because you can stuff it into the context and then just
 * call it to generate the HTML.  It can be used in other cases as
 * well, but WM is the best case for it right now.
 *
 * <p>For example code showing the usage for this module, please see
 * the toString() method below to see how it would be refered to from
 * WM.
 *
 * <pre>
 * // get the roles for a user
 * RoleSet userRoles = new DefaultAccessControl().getRoles(loginid, null);
 * if ( userRoles != null )
 * {
 *     context.put("hasRoleSet", Boolean.TRUE);
 *
 *     // get an array of the users roles
 *     Role[] usersRoles = userRoles.getRolesArray();
 *     // get an array of all the roles in the system
 *     Role[] allRoles = ((RoleSet)RolePeer.retrieveSet()).getRolesArray();
 *
 *     Object[] names = new Object[allRoles.length];
 *     Object[] values = new Object[allRoles.length];
 *     for ( int i=0;i<allRoles.length; i++ )
 *     {
 *         names[i] = new Integer(allRoles[i].getPrimaryKey()).toString();
 *         values[i] = allRoles[i].getName();
 *     }
 *
 *     SelectorBox sb = new SelectorBox("roleSetBox", names, values);
 *     sb.buildBooleans(usersRoles, allRoles);
 *     context.put("roleSetBox", sb);
 * }
 * else
 * {
 *     context.put("hasRoleSet", Boolean.FALSE);
 * }
 * </pre>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class SelectorBox
{
    /** This is the Select ECS element. */
    private Select sel = null;

    /** This is the size of the Select statement. */
    private int size = 1;

    /** This is the name= value. */
    private String name = null;

    /** This is the value= portion of the option element. */
    private Object[] names = null;

    /** This is the data after the option element. */
    private Object[] values = null;

    /** This is an array of which items are selected. */
    private boolean[] selected = null;

    /**
     * Generic constructor, builds a select box with a default size of
     * 1 and no selected items.
     *
     * @param name A String with the name for the select box.
     * @param names An Object[] with the names.
     * @param values An Object[] with the values.
     */
    public SelectorBox(String name, Object[] names, Object[] values)
    {
        this(name, names, values, 1, null);
    }

    /**
     * Generic constructor builds a select box.
     *
     * @param name A String with the name for the select box.
     * @param names An Object[] with the names.
     * @param values An Object[] with the values.
     * @param size An int specifying the size.
     */
    public SelectorBox(String name, Object[] names, Object[] values, int size)
    {
        this(name, names, values, size, null);
    }

    /**
     * Generic constructor builds a select box.
     *
     * @param name A String with the name for the select box.
     * @param names An Object[] with the names.
     * @param values An Object[] with the values.
     * @param selected A boolean[] with the selected items.
     */
    public SelectorBox(String name, Object[] names, Object[] values,
                       boolean[] selected)
    {
        this(name, names, values, 1, selected);
    }

    /**
     * Primary constructor for everything.
     *
     * @param name A String with the name for the select box.
     * @param names An Object[] with the names.
     * @param values An Object[] with the values.
     * @param size An int specifying the size.
     * @param selected A boolean[] with the selected items.
     */
    public SelectorBox(String name, Object[] names, Object[] values, int size,
                       boolean[] selected)
    {
        this.name = name;
        this.names = names;
        this.values = values;
        this.size = size;
        this.selected = selected;

        sel = new Select(name, size);
        sel.setName(name);
        sel.setSize(size);
    }

    /**
     * Pass in an array of selected items and the entire set of items
     * and it will determine which items in the selected set are also
     * in the entireset and then build a boolean[] up that is the same
     * size as the entireSet with markings to tell whether or not the
     * items are marked or not.  It uses toString().equalsIgnoreCase()
     * on the Object in the Object[] to determine if the items are
     * equal.
     *
     * @param selectedSet An Object[].
     * @param entireSet An Object[].
     */
    public void buildBooleans(Object[] selectedSet, Object[] entireSet)
    {
        selected = new boolean[entireSet.length];
        for (int j = 0; j < entireSet.length; j++)
        {
            Object r2 = entireSet[j];
            for (int i = 0; i < selectedSet.length; i++)
            {
                Object r1 = selectedSet[i];
                if (r1 != null && r2 != null &&
                        r1.toString().equalsIgnoreCase(r2.toString()))
                {
                    selected[j] = true;
                }
            }
        }
    }

    /**
     * This builds out the select box at a certain size.  To use this
     * element in WM, you simply build this object in your java code,
     * put it into the context and then call $selectBox.toString(5).
     *
     * @param size An int with the size.
     * @return A String with the HTML code.
     */
    public String toString(int size)
    {
        sel.setSize(size);
        sel.setName(name);
        for (int f = 0; f < values.length; f++)
        {
            Option opt = new Option((String) values[f]);
            opt.addElement((String) names[f]);
            if (selected != null && selected[f] == true)
            {
                opt.setSelected(true);
            }
            sel.addElement(opt);
        }
        String output = sel.toString();
        reset();
        return output;
    }

    /**
     * Resets the internal state of the SelectorBox.
     */
    public void reset()
    {
        sel = new Select(name, size);
    }

    /**
     * This builds out the select box at a certain size.  To use this
     * element in WM, you simply build this object in your java code,
     * put it into the context and then call $selectBox and it will
     * build it with the default size of 1.
     *
     * @return A String with the HTML code.
     */
    public String toString()
    {
        return this.toString(size);
    }

    /**
     * This allows you to set the multiple attribute to the select
     * element.  Example usage from within WM is like this:
     *
     * <p>
     * $selectBox.setMultiple(true).toString(4)
     *
     * @param val True if multiple selection should be allowed.
     * @return A SelectorBox (self).
     */
    public SelectorBox setMultiple(boolean val)
    {
        sel.setMultiple(val);
        return this;
    }

    /**
     * This allows one to set the name= attribute to the select
     * element.
     *
     * @param name A String with the name.
     * @return A SelectorBox (self).
     */
    public SelectorBox setName(String name)
    {
        this.name = name;
        sel.setName(name);
        return this;
    }

    /**
     * This allows one to set the size of the select element.
     *
     * @param size An int with the size.
     * @return A SelectorBox (self).
     */
    public SelectorBox setSize(int size)
    {
        this.size = size;
        sel.setSize(size);
        return this;
    }

    /**
     * This allows one to set an onChange attribute on the select tag
     *
     * @param script A string with the script to put in onChange
     * @return A SelectorBox (self).
     */
    public SelectorBox setOnChange(String script)
    {
        sel.setOnChange(script);
        return this;
    }

    /**
     * This allows one to set the array of selected booleans.
     *
     * @param an array of booleans
     * @return A SelectorBox (self).
     */
    public SelectorBox setSelected(boolean[] bools)
    {
        this.selected = bools;
        return this;
    }

    /**
     * This will set all elements as unselected, except for the
     * element(s) with the given name.
     *
     * @param name The name to appear as selected.
     * @return A SelectorBox (self).
     */
    public SelectorBox setSelected(Object name)
    {
        if (name != null)
        {
            selected = new boolean[names.length];
            for (int i = 0; i < names.length; i++)
            {
                Object o = names[i];
                if (o != null && o.toString().equalsIgnoreCase(name.toString()))
                {
                    selected[i] = true;
                }
            }
        }
        return this;
    }
}
