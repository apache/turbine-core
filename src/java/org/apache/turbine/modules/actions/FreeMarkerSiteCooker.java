package org.apache.turbine.modules.actions;

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

// Java Core Classes
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.net.URL;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

// Turbine Utility Classes
import org.apache.turbine.modules.Action;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.freemarker.DynamicURIModel;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;

import freemarker.template.SimpleScalar;

/**
 * This action parses all files located in the screens, navigations,
 * and layouts directories under the services.freemarker.path given in
 * TurbineResources.properties.  It cycles through links and parses
 * the responses for urls.  It writes the response to a file replacing
 * the urls with the static variation.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated you should use velocity
 */
public class FreeMarkerSiteCooker extends Action
{
    /**
     * Execute the action.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doPerform( RunData data )
        throws Exception
    {
        long start = System.currentTimeMillis();
        try
        {
            Hashtable links = new Hashtable();
            Vector files = new Vector();
            Object value = new Object();
            String basePath;
            basePath = TurbineResources.getString("services.freemarker.path");

            // Add all files under basePath to the files Vector.
            String[] templatePaths = {"layouts", "navigations", "screens"};
            for (int i=0; i<templatePaths.length; i++)
            {
                File tempPath = new File(basePath, templatePaths[i]);
                dirRecurse(tempPath, files);
            }

            // Parse the files looking for ${link("xxx")}.  Place the
            // List into links keyed by the template name.
            for (int i=0; i<files.size(); i++)
            {
                File file = (File)files.elementAt(i);
                BufferedReader in = null;
                char[] contents = null;
                try
                {
                    in = new BufferedReader(new FileReader(file));
                    parseTemplateFile(in, links);
                }
                finally
                {
                    if (in != null) in.close();
                }
            }

            // Now we have a Hashtable full of all the links on the
            // site.  Let's go through them create the url, hit the
            // server, parse the response, and save the translated
            // response to a file.
            int fileCount = 0;
            Enumeration e = links.keys();
            while (e.hasMoreElements())
            {
                String templateName = (String) e.nextElement();
                List alist = (List)links.get(templateName);
                DynamicURIModel uriModel = new DynamicURIModel(data);
                String uri =
                    ((SimpleScalar)uriModel.exec(alist)).getAsString();
                // System.out.println(uri);

                // Work around.  Do not have an ssl connection (not
                // really necessary for internal development, but
                // might want to add it later), so use http to get
                // response for parsing.
                String useUrl = null;
                String partialPath = null;
                if (uri.startsWith("https"))
                {
                    useUrl = "http" + uri.substring(5);
                    partialPath = "../cooked_ssl";
                }
                else
                {
                    useUrl = uri;
                    partialPath = "../cooked";
                }
                File cookedFile = new File(basePath +
                                           File.separator +
                                           partialPath, templateName);
                File parent = new File(cookedFile.getParent());
                if (!parent.exists() &&
                    !parent.equals(cookedFile))
                {
                    parent.mkdirs();
                }
                if (!cookedFile.exists())
                {
                    fileCount++;
                    System.out.println(fileCount + ": " + cookedFile);
                    // cookedFile.createNewFile();

                    BufferedReader in = null;
                    BufferedWriter[] out = new BufferedWriter[1];
                    try
                    {
                        out[0] = new BufferedWriter(new FileWriter(cookedFile));

                        URL url = new URL(useUrl);
                        in = new BufferedReader(
                            new InputStreamReader(url.openStream()));

                        parseFlatFile(uri, in, out);
                        System.out.println(fileCount + ": Successful");
                    }
                    finally
                    {
                        if (in != null) in.close();
                        if (out[0] != null) out[0].close();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }
        System.out.println("Time:" +
                           ((System.currentTimeMillis() - start)/1000) );

    }

    /**
     * Recurse through directories, adding all non-directorty entries
     * to the vector files.
     *
     * @param basePath The starting path to recurse into.
     * @param files The vector of gathered files.
     */
    private void dirRecurse(File basePath,
                            Vector files)
    {
        String[] dirList = basePath.list();
        for (int i=0; i<dirList.length; i++)
        {
            File tempPath = new File(basePath, dirList[i]);
            if (tempPath.isDirectory())
            {
                dirRecurse(tempPath, files);
            }
            else
            {
                files.addElement(tempPath);
            }
        }
    }

    /**
     * Parse a template file.
     *
     * @param in
     * @param links
     * @exception Exception, a generic exception.
     */
    private void parseTemplateFile(BufferedReader in,
                                   Hashtable links)
        throws Exception
    {
        char[] cbuf = new char[7];
        String templateName = null;
        StringBuffer argBuffer = new StringBuffer();
        LinkedList args = new LinkedList();
        boolean firstquote = false;
        boolean armed = false;
        int chint = in.read();
        while ( chint != -1)
        {
            char ch = (char)chint;
            if (armed)
            {
                // System.out.print(ch);
                if (ch == '\"')
                {
                    // Toggle between first and second quotes.
                    firstquote = !firstquote;
                    if (!firstquote)
                    {
                        // Finished an argument string.
                        String arg = argBuffer.toString();
                        args.add(arg);
                        argBuffer = new StringBuffer();

                        if ( !arg.startsWith("http") &&
                             templateName == null)
                        {
                            templateName = arg;
                        }
                    }
                }
                else if (firstquote)
                {
                    argBuffer.append(ch);
                }
                else if (ch == '}')
                {
                    // System.out.print("\ntemplateName: " +
                    //                  templateName + "\nArguments: ");
                    // Iterator iter = args.iterator();
                    // while (iter.hasNext())
                    // {
                    //     System.out.print((String)iter.next());
                    // }
                    // System.out.print("\n\n");

                    links.put(templateName, args);
                    templateName = null;
                    args = new LinkedList();
                    armed = false;
                }
            }
            else
            {
                for (int i=0; i<6; i++)
                {
                    cbuf[i] = cbuf[i+1];
                }
                cbuf[6] = ch;

                // Check to see if cbuf contains "${link(".
                if ( cbuf[0] == '$' &&
                     cbuf[1] == '{' &&
                     cbuf[2] == 'l' &&
                     cbuf[3] == 'i' &&
                     cbuf[4] == 'n' &&
                     cbuf[5] == 'k' &&
                     cbuf[6] == '(' )
                {
                    armed = true;
                }
            }
            chint = in.read();
        }
    }

    /**
     * Parse a flat file.
     *
     * @param url is the url of the current file which is being parsed.
     * @param in is the input stream of the file being parsed.
     * @param out is the output stream for the results.
     * @exception Exception, a generic exception.
     */
    private void parseFlatFile(String uri,
                               BufferedReader in,
                               BufferedWriter[] out)
        throws Exception
    {
        char[] cbuf = new char[4];
        String templateName = null;
        StringBuffer uriBuffer = new StringBuffer();
        boolean armed = false;
        int chint = in.read();
        while ( chint != -1)
        {
            char ch = (char)chint;
            // Write the current character to all the output streams.
            for (int i=0; i<out.length; i++)
            {
                out[i].write(ch);
            }

            // Found an href.
            if (armed)
            {
                // Found first quote which should surround the href
                // attribute's value.
                if (ch == '\"')
                {
                    parseUri(uri, in, out[0]);
                    armed = false;
                }
            }
            // Still looking for an href attribute.
            else
            {
                // Bumped all the characters in the buffer over one
                // char and add the current char.
                for (int i=0; i<3; i++)
                {
                    cbuf[i] = cbuf[i+1];
                }
                cbuf[3] = ch;

                // Check to see if cbuf contains href.
                if ( (cbuf[0] == 'h' || cbuf[0] == 'H') &&
                     (cbuf[1] == 'r' || cbuf[1] == 'R') &&
                     (cbuf[2] == 'e' || cbuf[2] == 'E') &&
                     (cbuf[3] == 'f' || cbuf[3] == 'F') )
                {
                    armed = true;
                }
            }
            // Get the next character and if it exists, loop back to
            // the top.
            chint = in.read();
        }
    }


    /**
     * Parse an URI.
     *
     * @param url is the url of the current file which is being parsed.
     * @param in is the input stream of the file being parsed.
     * @param out is the output stream for the results.
     * @exception Exception, a generic exception.
     */
    private void parseUri(String url,
                          BufferedReader in,
                          BufferedWriter out)
        throws Exception
    {
        // url is the url of the current file which is being parsed.
        // The 10 is for the first char after the '/'.
        int beginFile = url.indexOf("/template/") + 10;
        int endFile = url.indexOf('/', beginFile);
        if (endFile == -1)
        {
            endFile = url.indexOf('?', beginFile);
        }
        if (endFile == -1)
        {
            endFile = url.length();
        }
        String thisFilePath = url.substring(beginFile, endFile);
        // System.out.println("Url: " + url);
        char[] cbuf = new char[9];
        String templateName = null;
        StringBuffer uriBuffer = new StringBuffer();
        boolean armed = false;
        boolean sameHost = true;
        // The first char read is after the first quote surrounding
        // the href attribute's value.
        while (true)
        {
            char ch = (char)in.read();
            if (armed)
            {
                String urlPart = uriBuffer.toString();
                if (!url.regionMatches(0, urlPart, 0, urlPart.length()))
                {
                    System.out.println("Hosts do not match:");
                    System.out.println("This file's url starts with: " +
                                       url.substring(0,urlPart.length()));
                    System.out.println("The link starts with:        " +
                                       urlPart);
                    out.write(urlPart, 0, urlPart.length());
                    sameHost = false;
                }

                int i=0;
                int pathCounter = 0;
                boolean firstMiss = true;
                boolean missedAtLeastOnce = false;
                uriBuffer = new StringBuffer();
                while ( ch != '\"' && ch != '?')
                {
                    // System.out.println(ch + "\t" + thisFilePath.charAt(i));

                    // If previous characters have not been the same
                    // or the link is not to the same host with
                    // characters still being equal.
                    if ( missedAtLeastOnce ||
                         !(sameHost && ch == thisFilePath.charAt(i++)) )
                    {
                        if (sameHost && firstMiss)
                        {
                            // The paths are diverging so insert the
                            // correct number of "../".
                            missedAtLeastOnce = true;
                            firstMiss = false;
                            String endPath = thisFilePath.substring(i);
                            char[] endPathArray = endPath.toCharArray();
                            for (int j=0; j<endPathArray.length-2; j++)
                            {
                                if (endPathArray[j] == '%' &&
                                    endPathArray[j+1] == '2' &&
                                    ( endPathArray[j+2] == 'c' ||
                                      endPathArray[j+2] == 'C')
                                    )
                                {
                                    uriBuffer.append("../");
                                }
                            }
                        }

                        // Replace escaped commas with slashes and add
                        // the character to the buffer.
                        if (ch == '%')
                        {
                            char ch1 = (char)in.read();
                            char ch2 = (char)in.read();
                            if (ch1=='2' &&
                                (ch2=='c' ||
                                 ch2=='C'))
                            {
                                uriBuffer.append('/');
                            }
                            else
                            {
                                uriBuffer.append(ch);
                                uriBuffer.append(ch1);
                                uriBuffer.append(ch2);
                            }
                        }
                        else
                        {
                            uriBuffer.append(ch);
                        }
                    }
                    ch = (char)in.read();
                }

                // In the event we stopped due to a ?, add the query
                // data.
                if (ch == '?')
                {
                    while (ch != '\"')
                    {
                        // Until we get rid of the session id we will
                        // throw this away.
                        // uriBuffer.append(ch);
                        ch = (char)in.read();
                    }
                }
                uriBuffer.append('\"');
                out.write(uriBuffer.toString(), 0, uriBuffer.length());
                break;
            }
            // Have not found a template string yet.
            else
            {
                // Add the current char to the template checking
                // buffer and add it to the temp buffer for holding
                // the url.
                for (int i=0; i<8; i++)
                {
                    cbuf[i] = cbuf[i+1];
                }
                cbuf[8] = ch;
                uriBuffer.append(ch);

                // If we come upon a quote the url must have been to
                // an external site. So write the url and return.
                if (ch == '\"')
                {
                    out.write(uriBuffer.toString(), 0, uriBuffer.length());
                    break;
                }

                // Check to see if cbuf contains "template/".
                if (cbuf[0] == 't' &&
                    cbuf[1] == 'e' &&
                    cbuf[2] == 'm' &&
                    cbuf[3] == 'p' &&
                    cbuf[4] == 'l' &&
                    cbuf[5] == 'a' &&
                    cbuf[6] == 't' &&
                    cbuf[7] == 'e' &&
                    cbuf[8] == '/' )
                {
                    armed = true;
                    // Do not add "/template/" to the temp url buffer.
                    uriBuffer.setLength(uriBuffer.length()-10);
                }
            }
        }
    }
}
