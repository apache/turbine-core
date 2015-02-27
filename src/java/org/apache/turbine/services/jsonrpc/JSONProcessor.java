package org.apache.turbine.services.jsonrpc;

import java.io.CharArrayWriter;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONProcessor
{
    /** Log. */
    private static Log log = LogFactory.getLog(JSONProcessor.class);

    public static Object processCall(CharArrayWriter cdata, JSONRPCBridge json_bridge, HttpServletRequest request)
    {
        // Process the request
        JSONObject json_req = null;
        Object json_res = null;
        try
        {
            json_req = new JSONObject(cdata.toString());
            if (log.isDebugEnabled())
            {
                String methodName = json_req.getString("method");
                JSONArray arguments = json_req.getJSONArray("params");

                // If this a CallableReference it will have a non-zero objectID
                int object_id = json_req.optInt("objectID");
                StringBuffer sb = new StringBuffer(".doprocessCall(): call ");
                if (object_id != 0)
                {
                    sb.append("objectID=").append(object_id).append(" ");
                }
                sb.append(methodName).append("(").append(arguments).append(")");
                log.debug(sb.toString());
            }
            //json_res = json_bridge.call(new Object[] {request}, object_id, methodName, arguments);
            json_res = json_bridge.call(new Object[] {request}, json_req);
        }
        catch (JSONException e)
        {
            log.error(".processCall(): can't parse call: " + cdata, e);
            json_res = JSONRPCResult.MSG_ERR_PARSE;
        }
        // Write the response
        if (log.isDebugEnabled())
        {
            log.debug(".processCall():  returns " + json_res.toString());
        }
        return json_res;
    }

}
