package org.apache.turbine.services.jsp.tags;

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

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateLink;

/**
 * Supporting class for the TemplateLink tag.
 * Uses the TemplateLink class to construct a URI
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @version $Id$
 */
public class TemplateLinkTag extends TagSupport
{
     /**
      * template parameter defines the template to set
      * mandatory parameter
      */
     private String template;

     /**
      * The setter for template parameter
      */
     public void setTemplate(String template)
     {
         this.template = template;
     }

     /**
      * action parameter defines the action to set
      * optional parameter
      */
     private String action;

     /**
      * The setter for screen parameter
      */
     public void setAction(String action)
     {
         this.action = action;
     }

     public int doStartTag() throws JspException
     {
         RunData data = 
             (RunData)pageContext.getAttribute(JspService.RUNDATA, 
                                               PageContext.REQUEST_SCOPE);

         TemplateLink link = new TemplateLink( data );
         DynamicURI uri = link.setPage( template );
         if ( action != null ) uri = uri.setAction( action );

         try
         {
             if (uri != null) {
                 pageContext.getOut().print(uri.toString());
             }
         }
         catch (Exception e)
         {
             String message = 
                 "Error processing TemplateLink-tag, parameter: template='"
                 + template + "', action='" +action +"'";
             Log.error(message, e);
             try
             {
                 data.getOut().print( message );
             }
             catch(java.io.IOException ioe) {}
         }

         return EVAL_BODY_INCLUDE;
     }

}

