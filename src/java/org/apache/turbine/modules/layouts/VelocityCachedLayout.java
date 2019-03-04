package org.apache.turbine.modules.layouts;

import org.apache.turbine.modules.pages.DefaultPage;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This Layout module is Turbine 2.3.3 VelocityDirectLayout (same package)
 * with methods added for {@link PipelineData}. It is used in Jetspeed-1 portal.
 *
 * By using this layout any view write will immediately call the provided print writer {@link RunData#getOut()} and
 * the HTTP servlet response will be flushed and set the committed flag. This means of course
 * no change to the HTTP response header will be possible afterwards. By setting the {@link RunData#setAction(String)} in the request
 * (not only the model, but also) additional response headers could be set, cft. {@link DefaultPage#doBuild(PipelineData)}.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class VelocityCachedLayout extends VelocityDirectLayout
{
    /**
     * Render layout
     *
     * @param pipelineData PipelineData
     * @param context the Velocity context
     * @param templateName relative path to Velocity template
     *
     * @throws Exception if rendering fails
     */
    @Override
    protected void render(PipelineData pipelineData, Context context, String templateName)
        throws Exception
    {
        velocityService.handleRequest(context,
                prefix + templateName,
                pipelineData.getRunData().getOut());
    }
}

