package org.apache.turbine.services.schedule;

/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2001-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 *  4. The names "Apache" and "Apache Software Foundation" and "Apache Turbine"
 * must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact
 * apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", "Apache
 * Turbine", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 */

import java.util.Date;

import org.apache.turbine.util.TurbineException;

/**
 * This is a interface for a scheduled job. It does not specify how to 
 * configure when to run, that is left to subclasses.  See the JobEntryTorque
 * for an example of a JobEntry backed by Torque objects.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface JobEntry extends Comparable
{

    /**
	 * Used for ordering Jobentries Note: this comparator imposes orderings
	 * that are inconsistent with equals.
	 * 
	 * @param je The first <code>JobEntry</code> object.
	 * @return An <code>int</code> indicating the result of the comparison.
	 */
    public int compareTo(Object je);

    /**
	 * Sets whether the job is running.
	 * 
	 * @param isActive Whether the job is running.
	 */
    public void setActive(boolean isActive);

    /**
	 * Check to see if job is currently active/running
	 * 
	 * @return true if job is currently geing run by the workerthread,
	 *         otherwise false
	 */
    public boolean isActive();

    
	/**
	 * Get the Task
	 *
	 * @return String
	 */
	public String getTask();
	
	/**
	  * Set the value of Task
	  *
	  * @param v new value
	  */
	 public void setTask(String v);	
    /**
	 * Get the next runtime for this job as a long.
	 * 
	 * @return The next run time as a long.
	 */
    public long getNextRuntime();

    /**
	 * Gets the next runtime as a date
	 * 
	 * @return Next run date
	 */
    public Date getNextRunDate();

    /**
	 * Get the next runtime for this job as a String.
	 * 
	 * @return The next run time as a String.
	 */
    public String getNextRunAsString();

    /**
	 * Calculate how long before the next runtime. <br>
	 * 
	 * The runtime determines it's position in the job queue. Here's the logic:
	 * <br>
	 *  1. Create a date the represents when this job is to run. <br>
	 *  2. If this date has expired, them "roll" appropriate date fields
	 * forward to the next date. <br>
	 *  3. Calculate the diff in time between the current time and the next run
	 * time. <br>
	 * 
	 * @exception TurbineException a generic exception.
	 */
    public void calcRunTime() throws TurbineException;
    
}
