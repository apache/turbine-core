package org.apache.turbine.util.migrator;

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

import java.io.File;
import java.io.FileWriter;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.velocity.util.StringUtils;
import org.apache.tools.ant.DirectoryScanner;

/**
 * This utility class attempts to help Turbine developers migrate
 * their wepapp sources across versions of Turbine by applying
 * a series of regexes and rules to a source tree and creates
 * a new source tree that is compatible with subsequent
 * releases of Turbine.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * 
 * @version $Id$
 */
public class Migrator
{
    /**
     * Source file currently being migrated.
     */
    protected String originalSourceFile;

    /**
     * Regular expression tool 
     */
    protected Perl5Util perl;
    
    /** 
     * Path separator property 
     */
    protected String pathSeparator = File.separator;
    
    /*
     * The regexes to use for substition. The regexes come
     * in pairs. The first is the string to match, the
     * second is the substitution to make.
     *
     * These have to be taken from a configuration file
     * and we have to store the regexes in groups according
     * to what version of Turbine they can be applied to.
     *
     * Should be taken from an XML file, or a configuration
     * file.
     */
    protected String[] res =
    {
        // Make #if directive match the Velocity directive style.
        "#if\\s*[(]\\s*(.*\\S)\\s*[)]\\s*(#begin|{)[ \\t]?",
        "#if( $1 )",

        // Remove the WM #end #else #begin usage.
        "[ \\t]?(#end|})\\s*#else\\s*(#begin|{)[ \\t]?(\\w)",
        "#else#**#$3", // avoid touching a followup word with embedded comment
        "[ \\t]?(#end|})\\s*#else\\s*(#begin|{)[ \\t]?",
        "#else",

        // Convert WM style #foreach to Velocity directive style.
        "#foreach\\s+(\\$\\w+)\\s+in\\s+(\\$[^\\s#]+)\\s*(#begin|{)[ \\t]?",
        "#foreach( $1 in $2 )",

        // Change the "}" to #end. Have to get more
        // sophisticated here. Will assume either {}
        // and no javascript, or #begin/#end with the
        // possibility of javascript.
        "\n}", // assumes that javascript is indented, WMs not!!!
        "\n#end",
        
        // Convert WM style #set to Velocity directive style.
        "#set\\s+(\\$[^\\s=]+)\\s*=\\s*(.*\\S)[ \\t]*",
        "#set( $1 = $2 )",
        "(##[# \\t\\w]*)\\)", // fix comments included at end of line
        ")$1",

        // Convert WM style #parse to Velocity directive style.
        "#parse\\s+([^\\s#]+)[ \\t]?",
        "#parse( $1 )",

        // Convert WM style #include to Velocity directive style.
        "#include\\s+([^\\s#]+)[ \\t]?",
        "#include( $1 )",

        // Convert WM formal reference to VL syntax.
        "\\$\\(([^\\)]+)\\)",
        "${$1}",
        "\\${([^}\\(]+)\\(([^}]+)}\\)", // fix encapsulated brakets: {(})
        "${$1($2)}",

        // Velocity currently does not permit leading underscore.
        "\\$_",
        "$l_",
        "\\${(_[^}]+)}", // within a formal reference
        "${l$1}",
            
        // Change extensions when seen.
        "\\.wm",
        ".vm"
    };
    
    /**
     * Iterate through the set of find/replace regexes
     * that will convert a given source tree from one
     */
    public void migrate(String args[])
    {
        if (args.length < 1)
        {
            usage();
        }            
        
        File file = new File(args[0]);
        
        if (!file.exists())
        {
            System.err.println(
                "The specified template or directory does not exist");
            
            System.exit(1);
        }
        
        if (file.isDirectory())
        {
            String basedir = args[0];
            String newBasedir = basedir + ".new";
            
            DirectoryScanner ds = new DirectoryScanner();
            ds.setBasedir(basedir);
            ds.addDefaultExcludes();
            ds.scan();
            String[] files = ds.getIncludedFiles();
            
            for (int i = 0; i < files.length; i++)
            {
                writeSource(files[i], basedir, newBasedir);
            }                
        }
        else
        {
            writeSource(args[0], "", "");
        }
    }

    /**
     * Write out the converted template to the given named file
     * and base directory.
     */
    private boolean writeSource(String file, String basedir, String newBasedir)
    {
        System.out.println("Converting " + file + "...");
        
        String sourceFile;
        String sourceDir;
        String newSourceFile;
        File outputDirectory;
        
        if (basedir.length() == 0)
        {
            sourceFile = file;
            sourceDir = "";
            newSourceFile = file;
        }            
        else
        {
            sourceFile = basedir + pathSeparator + file;
            sourceDir = newBasedir + pathSeparator + 
                file.substring(0, file.lastIndexOf(pathSeparator));
        
            outputDirectory = new File(sourceDir);
                
            if (! outputDirectory.exists())
            {
                outputDirectory.mkdirs();
            }                
                
            newSourceFile = newBasedir + pathSeparator + file;
        }            
        
        String convertedSourceFile = convertSourceFile(sourceFile);
                    
        try
        {
            FileWriter fw = new FileWriter(newSourceFile);
            fw.write(convertedSourceFile);
            fw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    
        return true;
    }

    /**
     * How to use this little puppy :-)
     */
    public void usage()
    {
        System.err.println("Usage: directory");
        System.exit(1);
    }

    /**
     * Apply find/replace regexes to our Turbine source file.
     *
     * @param String source file to migrate.
     * @return String migrated source file.
     */
    public String convertSourceFile(String sourceFile)
    {
        originalSourceFile = StringUtils.fileContentsToString(sourceFile);

        /*
         * overcome current velocity 0.71 limitation.
         * Hmm. Not sure if this applies to source files.
         * I will try this.
         */
        if ( !originalSourceFile.endsWith("\n") )
        {
          originalSourceFile += "\n";
        }          

        perl = new Perl5Util();
        for (int i = 0; i < res.length; i += 2)
        {
            while (perl.match("/" + res[i] + "/", originalSourceFile))
            {
                originalSourceFile = perl.substitute(
                    "s/" + res[i] + "/" + res[i+1] + "/", originalSourceFile);
            }
        }

        return originalSourceFile;
    }

    /**
     * Main hook for the conversion process.
     */
    public static void main(String[] args)
    {
        Migrator migrator = new Migrator();
        migrator.migrate(args);
    }
}
