package org.apache.turbine.util.db;

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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.util.StringTokenizer;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.util.TurbineException;

/**
 * <p>This class generates universally unique id's in the form of a String.
 * The id has three parts.  The first is supposed to be location dependent.
 * The preferred location parameter is an ethernet (MAC) address, but an IP
 * can be used as well.  if none is supplied a Math.random generated number
 * will be used.  This part of the key will be 48 bits in length.
 * The second part of the key is time related and will be the lower 48 bits
 * of the long used to signify the time since Jan. 1, 1970.  This will
 * cause key rollover in the year 6429.
 * The preceding 12 bytes are Base64 encoded with the characters / and *
 * replaced by _ (underscore) and - (dash).  Resulting in 16 characters.
 * Finally a counter is used to hand out 4095 keys in between each
 * timestamp.
 * The resulting id is a String of 18 characters including:
 * a-z,A-Z,0-9, and the previously mentioned - and _.</p>
 *
 * <p>Note this class does not save any state information, so it is important
 * that time only moves forward to keep the integrity of the ids.  We
 * might want to consider saving some state info.</p>
 *
 * <p>To specify the MAC/Ethernet address, add a uuid.address= property to the
 * TurbineResources.properties file.</p>
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class UUIdGenerator
{
    /** Logging */
    private static Log log = LogFactory.getLog(UUIdGenerator.class);

    private static final String errorString = "uuid.address property in "
            + "TurbineResources.properties should be a valid IP\n "
            + "e.g. 18.2.3.100, or an ethernet address e.g. "
            + "AE:10:3E:de:f5:77 uuid.address was ";

    private byte[] address = new byte[6];
    private String baseId = null;
    private int counter = 0;

    /**
     * Constructor
     */
    public UUIdGenerator() throws TurbineException
    {
        String addr = 
            Turbine.getConfiguration().getString(TurbineConstants.UUID_ADDRESS_KEY);

        if (StringUtils.isEmpty(addr))
        {
            log.info("UUIdGenerator is using a random number as the "
                    + "base for id's.  This is not the best method for many "
                    + "purposes, but may be adequate in some circumstances."
                    + " Consider using an IP or ethernet (MAC) address if "
                    + "available. Edit TurbineResources.properties file and "
                    + "add a uuid.address= property.");

            for (int i = 0; i < 6; i++)
            {
                address[i] = (byte) (255 * Math.random());
            }
        }
        else
        {
            if (addr.indexOf(".") > 0)
            {
                // we should have an IP
                StringTokenizer stok = new StringTokenizer(addr, ".");
                if (stok.countTokens() != 4)
                {
                    throw new TurbineException(errorString + addr);
                }
                // this is meant to insure that id's made from ip addresses
                // will not conflict with MAC id's. I think MAC addresses
                // will never have the highest bit set.  Though this should
                // be investigated further.
                address[0] = (byte) 255;
                address[1] = (byte) 255;
                int i = 2;
                try
                {
                    while (stok.hasMoreTokens())
                    {
                        address[i++] =
                                Integer.valueOf(stok.nextToken(),
                                        16).byteValue();
                    }
                }
                catch (Exception e)
                {
                    throw new TurbineException(errorString + addr, e);
                }
            }
            else if (addr.indexOf(":") > 0)
            {
                // we should have a MAC
                StringTokenizer stok = new StringTokenizer(addr, ":");
                if (stok.countTokens() != 6)
                {
                    throw new TurbineException(errorString + addr);
                }
                int i = 0;
                try
                {
                    while (stok.hasMoreTokens())
                    {
                        address[i++] = Byte.parseByte(stok.nextToken(), 16);
                    }
                }
                catch (Exception e)
                {
                    throw new TurbineException(errorString + addr, e);
                }
            }
            else
            {
                throw new TurbineException(errorString + addr);
            }
        }
    }

    /**
     * Generates the new base id
     */
    private final void generateNewBaseId() throws Exception
    {
        long now = System.currentTimeMillis();
        byte[] nowBytes = org.apache.java.lang.Bytes.toBytes(now);
        ByteArrayOutputStream bas = null;
        OutputStream encodedStream = null;
        try
        {
            bas = new ByteArrayOutputStream(16);
            encodedStream = MimeUtility.encode(bas, "base64");
            encodedStream.write(nowBytes);
            baseId = bas.toString("ISO-8859-1"); // or maybe "US-ASCII"?
            baseId = baseId.replace('/', '_');
            baseId = baseId.replace('*', '-');
        }
        finally
        {
            if (bas != null)
            {
                bas.close();
            }
            if (encodedStream != null)
            {
                encodedStream.close();
            }
        }
    }

    /**
     * Gets the id
     * @return the 18 character id
     */
    public String getId() throws Exception
    {
        int index = ++counter;
        if (index > 4095)
        {
            synchronized (this)
            {
                if (counter > 4095)
                {
                    generateNewBaseId();
                    counter = 0;
                }
                else
                {
                    index = ++counter;
                }
            }
        }
        StringBuffer idbuf = new StringBuffer(18);
        idbuf.append(baseId);
        idbuf.append(countChar[index / 64]);
        idbuf.append(countChar[index % 64]);
        return idbuf.toString();
    }

    /**
     * characters used in the ID
     */
    private static final char[] countChar =
            {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', '-', '_'
            };
}

