/*
 * Created on 16-Jul-2004
 *
 */
package org.apache.turbine.services.pull;

import org.apache.turbine.pipeline.PipelineData;


/**
 * Interface for tools to be init'd and refreshed using a PipelineData
 * object 
 * Code largely taken from ApplicationTool. 
 * 
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public interface PipelineDataApplicationTool {

    /**
     * Initialize the application tool. The data parameter holds a different
     * type depending on how the tool is being instantiated:
     * <ul>
     * <li>For global tools data will be null</li>
     * <li>For request tools data will be of type PipelineData</li>
     * <li>For session and authorized tools data will be of type User</li>
     * </ul>
     * <p>
     * It is possible that session scope tools will be initialized with a null
     * <code>User</code> object.  This happens when the first request on a
     * session happens to the be login action.  The next request on the session
     * will cause the session tool to be refreshed if
     * <code>tools.per.request.refresh</code> is set to <code>true</code>
     * in <code>TurbineResources.properties</code>.  You will then be able to
     * get a <code>User</code> object from the instance of
     * <code>RunData</object>.
     *
     * @param data initialization data
     */
    public void init(Object data);

    /**
     * Refresh the application tool. This is
     * necessary for development work where you
     * probably want the tool to refresh itself
     * if it is using configuration information
     * that is typically cached after initialization
     *
     * @param data The current PipelineData Object
     */
    public void refresh(PipelineData data);

    
}
