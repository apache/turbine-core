package org.apache.turbine.test;

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

import com.mockobjects.servlet.MockHttpSession;
/**
 * Extension to the basic MockHttpSession to provide some extra parameters
 * required by Turbine.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class EnhancedMockHttpSession extends MockHttpSession
{
    private boolean isNew = true;
    private int maxInactiveInterval =0;
    
    /**
     *
     */
    public EnhancedMockHttpSession()
    {
        super();
    }
    /**
     * The default MockHttpSession doesn't implement this method.  It always
     * returns true.
     */
    public boolean isNew()
    {
        return isNew;
    }
    
    public void setMaxInactiveInterval(int maxInactiveInterval){
        this.maxInactiveInterval =maxInactiveInterval;
    }
    
    public int getMaxInactiveInterval(){
        return maxInactiveInterval;
    }
    
    /**
     * The underlying mock objects throws an Assert failure if we don't have
     * an attribute.  However, in Turbine, getting a null is okay, it just 
     * means we haven't put the object in yet!
     */
    public Object getAttribute(String attributeName)
    {
        try
        {
            return super.getAttribute(attributeName);
        }
        catch (junit.framework.AssertionFailedError afe)
        {
            return null;
        }
    }
}
