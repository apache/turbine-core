package org.apache.turbine.services.intake;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.om.Retrievable;
import org.apache.turbine.services.intake.model.Group;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.ValueParser;
import org.apache.turbine.util.pool.Recyclable;

/**
 * The main class through which Intake is accessed.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class IntakeTool
        implements ApplicationTool, Recyclable
{
    /** Used for logging */
    private static Log log = LogFactory.getLog(IntakeTool.class);

    /** Constant for default key */
    public static final String DEFAULT_KEY = "_0";

    /** Groups from intake.xml */
    private HashMap groups;

    /** ValueParser instance */
    private ValueParser pp;

    HashMap declaredGroups = new HashMap();
    StringBuffer allGroupsSB = new StringBuffer(256);
    StringBuffer groupSB = new StringBuffer(128);

    /** The cache of PullHelpers. **/
    private Map pullMap;

    /**
     * Constructor
     */
    public IntakeTool()
    {
        String[] groupNames = TurbineIntake.getGroupNames();
        int groupCount = 0;
        if (groupNames != null)
        {
            groupCount = groupNames.length;
        }
        groups = new HashMap((int) (1.25 * groupCount + 1));
        pullMap = new HashMap((int) (1.25 * groupCount + 1));

        for (int i = groupCount - 1; i >= 0; i--)
        {
            pullMap.put(groupNames[i], new PullHelper(groupNames[i]));
        }
    }

    /**
     * Prepares intake for a single request
     */
    public void init(Object runData)
    {
        this.pp = ((RunData) runData).getParameters();

        String[] groupKeys = pp.getStrings("intake-grp");
        String[] groupNames = null;
        if (groupKeys == null || groupKeys.length == 0)
        {
            groupNames = TurbineIntake.getGroupNames();
        }
        else
        {
            groupNames = new String[groupKeys.length];
            for (int i = groupKeys.length - 1; i >= 0; i--)
            {
                groupNames[i] = TurbineIntake.getGroupName(groupKeys[i]);
            }

        }

        for (int i = groupNames.length - 1; i >= 0; i--)
        {
            try
            {
                List foundGroups = TurbineIntake.getGroup(groupNames[i])
                        .getObjects(pp);

                if (foundGroups != null)
                {
                    for (Iterator iter = foundGroups.iterator();
                         iter.hasNext();)
                    {
                        Group group = (Group) iter.next();
                        groups.put(group.getObjectKey(), group);
                    }
                }
            }
            catch (Exception e)
            {
                log.error(e);
            }
        }
    }

    public void addGroupsToParameters(ValueParser vp)
    {
        for (Iterator i = groups.values().iterator(); i.hasNext();)
        {
            Group group = (Group) i.next();
            if (!declaredGroups.containsKey(group.getIntakeGroupName()))
            {
                declaredGroups.put(group.getIntakeGroupName(), null);
                vp.add("intake-grp", group.getGID());
            }
            vp.add(group.getGID(), group.getOID());
        }
        declaredGroups.clear();
    }

    /**
     * A convenience method to write out the hidden form fields
     * that notify intake of the relevant groups.  It should be used
     * only in templates with 1 form.  In multiform templates, the groups
     * that are relevant for each form need to be declared using
     * $intake.newForm() and $intake.declareGroup($group) for the relevant
     * groups in the form.
     *
     */
    public String declareGroups()
    {
        allGroupsSB.setLength(0);
        for (Iterator i = groups.values().iterator(); i.hasNext();)
        {
            declareGroup((Group) i.next(), allGroupsSB);
        }
        return allGroupsSB.toString();
    }

    /**
     * A convenience method to write out the hidden form fields
     * that notify intake of the group.
     */
    public String declareGroup(Group group)
    {
        groupSB.setLength(0);
        declareGroup(group, groupSB);
        return groupSB.toString();
    }

    /**
     * xhtml valid hidden input field(s) that notifies intake of the
     * group's presence.
     */
    public void declareGroup(Group group, StringBuffer sb)
    {
        if (!declaredGroups.containsKey(group.getIntakeGroupName()))
        {
            declaredGroups.put(group.getIntakeGroupName(), null);
            sb.append("<input type=\"hidden\" name=\"")
                    .append("intake-grp\" value=\"")
                    .append(group.getGID())
                    .append("\"/>\n");
        }
        group.appendHtmlFormInput(sb);
    }

    public void newForm()
    {
        declaredGroups.clear();
        for (Iterator i = groups.values().iterator(); i.hasNext();)
        {
            ((Group) i.next()).resetDeclared();
        }
    }

    /**
     * Implementation of ApplicationTool interface is not needed for this
     * tool as it is request scoped
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Inner class to present a nice interface to the template designer
     */
    public class PullHelper
    {
        /** Name of the group used by the pull helper */
        String groupName;

        /**
         * Private constructor to force use of factory method.
         *
         * @param groupName
         */
        private PullHelper(String groupName)
        {
            this.groupName = groupName;
        }

        /**
         * Populates the object with the default values from the XML File
         *
         * @return a Group object with the default values
         * @throws IntakeException
         */
        public Group getDefault()
                throws IntakeException
        {
            return setKey(DEFAULT_KEY);
        }

        /**
         * Calls setKey(key,true)
         *
         * @param key
         * @return an Intake Group
         * @throws IntakeException
         */
        public Group setKey(String key)
                throws IntakeException
        {
            return setKey(key, true);
        }

        /**
         *
         * @param key
         * @param create
         * @return an Intake Group
         * @throws IntakeException
         */
        public Group setKey(String key, boolean create)
                throws IntakeException
        {
            Group g = null;

            String inputKey = TurbineIntake.getGroupKey(groupName) + key;
            if (groups.containsKey(inputKey))
            {
                g = (Group) groups.get(inputKey);
            }
            else if (create)
            {
                g = TurbineIntake.getGroup(groupName);
                groups.put(inputKey, g);
                g.init(key, pp);
            }

            return g;
        }

        /**
         * maps an Intake Group to the values from a Retrievable object.
         *
         * @param obj A retrievable object
         * @return an Intake Group
         */
        public Group mapTo(Retrievable obj)
                throws IntakeException
        {
            Group g = null;

            try
            {
                String inputKey = TurbineIntake.getGroupKey(groupName)
                        + obj.getQueryKey();
                if (groups.containsKey(inputKey))
                {
                    g = (Group) groups.get(inputKey);
                }
                else
                {
                    g = TurbineIntake.getGroup(groupName);
                    groups.put(inputKey, g);
                }
                return g.init(obj);
            }
            catch (Exception e)
            {
                log.error(e);
            }

            return null;
        }
    }

    /**
     * get a specific group
     */
    public PullHelper get(String groupName)
            throws IntakeException
    {
        return (PullHelper) pullMap.get(groupName);
    }

    /**
     * Get a specific group
     *
     * @param throwExceptions if false, exceptions will be supressed.
     * @throws IntakeException could not retrieve group
     */
    public PullHelper get(String groupName, boolean throwExceptions)
            throws IntakeException
    {
        return (PullHelper) pullMap.get(groupName);
    }

    /**
     * Loops through all of the Groups and checks to see if
     * the data within the Group is valid.
     */
    public boolean isAllValid()
    {
        boolean allValid = true;
        for (Iterator iter = groups.values().iterator(); iter.hasNext();)
        {
            Group group = (Group) iter.next();
            allValid &= group.isAllValid();
        }
        return allValid;
    }

    /**
     * Get a specific group by name and key.
     */
    public Group get(String groupName, String key)
            throws IntakeException
    {
        if (groupName == null)
        {
            throw new IntakeException("Intake.get: groupName == null");
        }
        if (key == null)
        {
            throw new IntakeException("Intake.get: key == null");
        }
        return ((PullHelper) get(groupName)).setKey(key);
    }

    /**
     * Get a specific group by name and key. Also specify
     * whether or not you want to create a new group.
     */
    public Group get(String groupName, String key, boolean create)
            throws IntakeException
    {
        return ((PullHelper) get(groupName)).setKey(key, create);
    }

    /**
     * Removes group.  Primary use is to remove a group that has
     * been processed by an action and is no longer appropriate
     * in the view (screen).
     */
    public void remove(Group group)
    {
        groups.remove(group.getObjectKey());
        group.removeFromRequest();

        try
        {
            TurbineIntake.releaseGroup(group);
        }
        catch (TurbineException se)
        {
            log.error("Tried to release unknown group "
                    + group.getIntakeGroupName());
        }
    }

    /**
     * Removes all groups.  Primary use is to remove groups that have
     * been processed by an action and are no longer appropriate
     * in the view (screen).
     */
    public void removeAll()
    {
        Object[] allGroups = groups.values().toArray();
        for (int i = allGroups.length - 1; i >= 0; i--)
        {
            Group group = (Group) allGroups[i];
            remove(group);
        }
    }

    /**
     * Get a Map containing all the groups.
     *
     * @return the Group Map
     */
    public Map getGroups()
    {
        return groups;
    }

    // ****************** Recyclable implementation ************************

    private boolean disposed;

    /**
     * Recycles the object for a new client. Recycle methods with
     * parameters must be added to implementing object and they will be
     * automatically called by pool implementations when the object is
     * taken from the pool for a new client. The parameters must
     * correspond to the parameters of the constructors of the object.
     * For new objects, constructors can call their corresponding recycle
     * methods whenever applicable.
     * The recycle methods must call their super.
     */
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object after use. The method is called
     * when the object is returned to its pool.
     * The dispose method must call its super.
     */
    public void dispose()
    {
        for (Iterator iter = groups.values().iterator(); iter.hasNext();)
        {
            Group g = (Group) iter.next();

            try
            {
                TurbineIntake.releaseGroup(g);
            }
            catch (TurbineException se)
            {
                log.error("Tried to release unknown group "
                        + g.getIntakeGroupName());
            }
        }

        groups.clear();
        declaredGroups.clear();
        pp = null;

        disposed = true;
    }

    /**
     * Checks whether the recyclable has been disposed.
     *
     * @return true, if the recyclable is disposed.
     */
    public boolean isDisposed()
    {
        return disposed;
    }
}
