package org.apache.turbine.services.intake;


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


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.intake.IntakeException;
import org.apache.fulcrum.intake.IntakeServiceFacade;
import org.apache.fulcrum.intake.Retrievable;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.parser.ValueParser;
import org.apache.fulcrum.pool.Recyclable;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;


/**
 * The main class through which Intake is accessed.  Provides easy access
 * to the Fulcrum Intake component.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class IntakeTool
        implements ApplicationTool, Recyclable
{
    /** Used for logging */
    protected static Log log = LogFactory.getLog(IntakeTool.class);

    /** Constant for default key */
    public static final String DEFAULT_KEY = "_0";

    /** Constant for the hidden fieldname */
    public static final String INTAKE_GRP = "intake-grp";

    /** Groups from intake.xml */
    protected HashMap<String, Group> groups;

    /** ValueParser instance */
    protected ValueParser pp;

    private final HashMap<String, Group> declaredGroups = new HashMap<String, Group>();
    private final StringBuffer allGroupsSB = new StringBuffer(256);
    private final StringBuffer groupSB = new StringBuffer(128);

    /** The cache of PullHelpers. **/
    private final Map<String, IntakeTool.PullHelper> pullMap;

    /**
     * Constructor
     */
    @SuppressWarnings("null")
    public IntakeTool()
    {
        String[] groupNames = IntakeServiceFacade.getGroupNames();
        int groupCount = 0;
        if (groupNames != null)
        {
            groupCount = groupNames.length;
        }
        groups = new HashMap<String, Group>((int) (1.25 * groupCount + 1));
        pullMap = new HashMap<String, IntakeTool.PullHelper>((int) (1.25 * groupCount + 1));

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

        String[] groupKeys = pp.getStrings(INTAKE_GRP);
        String[] groupNames = null;
        if (groupKeys == null || groupKeys.length == 0)
        {
            groupNames = IntakeServiceFacade.getGroupNames();
        }
        else
        {
            groupNames = new String[groupKeys.length];
            for (int i = groupKeys.length - 1; i >= 0; i--)
            {
                groupNames[i] = IntakeServiceFacade.getGroupName(groupKeys[i]);
            }

        }

        for (int i = groupNames.length - 1; i >= 0; i--)
        {
            try
            {
                List foundGroups = IntakeServiceFacade.getGroup(groupNames[i])
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
            catch (IntakeException e)
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
                    .append(INTAKE_GRP)
                    .append("\" value=\"")
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
         * Protected constructor to force use of factory method.
         *
         * @param groupName
         */
        protected PullHelper(String groupName)
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

            String inputKey = IntakeServiceFacade.getGroupKey(groupName) + key;
            if (groups.containsKey(inputKey))
            {
                g = groups.get(inputKey);
            }
            else if (create)
            {
                g = IntakeServiceFacade.getGroup(groupName);
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
        {
            Group g = null;

            try
            {
                String inputKey = IntakeServiceFacade.getGroupKey(groupName)
                        + obj.getQueryKey();
                if (groups.containsKey(inputKey))
                {
                    g = groups.get(inputKey);
                }
                else
                {
                    g = IntakeServiceFacade.getGroup(groupName);
                    groups.put(inputKey, g);
                }

                return g.init(obj);
            }
            catch (IntakeException e)
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
    {
        return pullMap.get(groupName);
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
        return pullMap.get(groupName);
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
            throw new IntakeException("IntakeServiceFacade.get: groupName == null");
        }
        if (key == null)
        {
            throw new IntakeException("IntakeServiceFacade.get: key == null");
        }

        PullHelper ph = get(groupName);
        return (ph == null) ? null : ph.setKey(key);
    }

    /**
     * Get a specific group by name and key. Also specify
     * whether or not you want to create a new group.
     */
    public Group get(String groupName, String key, boolean create)
            throws IntakeException
    {
        if (groupName == null)
        {
            throw new IntakeException("IntakeServiceFacade.get: groupName == null");
        }
        if (key == null)
        {
            throw new IntakeException("IntakeServiceFacade.get: key == null");
        }

        PullHelper ph = get(groupName);
        return (ph == null) ? null : ph.setKey(key, create);
    }

    /**
     * Removes group.  Primary use is to remove a group that has
     * been processed by an action and is no longer appropriate
     * in the view (screen).
     */
    public void remove(Group group)
    {
        if (group != null)
        {
            groups.remove(group.getObjectKey());
            group.removeFromRequest();

            String[] groupKeys = pp.getStrings(INTAKE_GRP);

            pp.remove(INTAKE_GRP);

			if (groupKeys != null)
			{
		        for (int i = 0; i < groupKeys.length; i++)
		        {
		            if (!groupKeys[i].equals(group.getGID()))
		            {
		                 pp.add(INTAKE_GRP, groupKeys[i]);
		            }
                }
		    }


            try
            {
                IntakeServiceFacade.releaseGroup(group);
            }
            catch (IntakeException ie)
            {
                log.error("Tried to release unknown group "
                        + group.getIntakeGroupName());
            }
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
                IntakeServiceFacade.releaseGroup(g);
            }
            catch (IntakeException ie)
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
