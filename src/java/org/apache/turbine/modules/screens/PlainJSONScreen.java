package org.apache.turbine.modules.screens;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Screen class for dealing with JSON requests.  Typically you would
 * extend this class and override the doOutput() method to use it by setting the JSON output into
 * rundata.setMessage( serialized ).
 * As convenience you may use inject in your extended class the Turbine service JsonService
 * Use {@link PlainJSONSecureAnnotatedScreen} if you need the user to be
 * logged in or having a special role in prior to executing the functions you provide.
 *
 * <p>Here is an example from a subclass:
 *
 * <code>
 * 
 *
 * public void doOutput(PipelineData pipelineData) throws Exception
 * {
 *     RunData data = getRunData(pipelineData);
 *     JSONStrategy strategy = null;
 *     
 *     try
 *     {
 *        strategy = new XYStrategy();
 *        // the result goes into rundata.message
 *        strategy.execute(data, jsonService);
 *     }
 *       catch ( Exception e )
 *       {
 *          log.error( "init failed for "+strategy , e);
 *          String msg = new JSONObject().put("error", e.getMessage()).toString();
 *          data.setMessage( msg );
 *       }
 *     
 *     super.doOutput(data);
 * }
 * </code>
 *
 *
 * @author gk
 * @version $Id$
 */
public class PlainJSONScreen extends RawScreen
{
    protected static final String JSON_TYPE = "application/json;charset=utf-8";

    protected final static int BUFFER_SIZE = 4096;
    
    static final Logger log = LoggerFactory.getLogger(PlainJSONScreen.class);

    /** Injected service instance */
    //@TurbineService
    //protected JsonService jsonService;

    /**
     * @see org.apache.turbine.modules.screens.RawScreen#getContentType(org.apache.turbine.pipeline.PipelineData)
     */
    @Override
    protected String getContentType(PipelineData pipelineData)
    {
        return JSON_TYPE;
    }

    /**
     * Output JSON content set into {@link RunData#getMessage()}.
     *
     * Encoding is UTF-8. @{@link #JSON_TYPE}: {@value #JSON_TYPE}.
     *
     * @param pipelineData The PipelineData object.
     */
    @Override
    protected void doOutput(PipelineData pipelineData) throws Exception
    {
        RunData data = getRunData(pipelineData);
       
        HttpServletRequest request = data.getRequest();
        // read in json!
        String charset =  "UTF-8"; //request.getCharacterEncoding();
        
        String json_res =data.getMessage();

        log.debug( "json_res output:" +json_res );
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(data.getResponse().getOutputStream(),charset));
        out.print(json_res.toString());
        out.flush();
        out.close();
    }
}
