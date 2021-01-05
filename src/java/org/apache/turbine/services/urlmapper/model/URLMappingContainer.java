package org.apache.turbine.services.urlmapper.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * URL Map Container Model Class
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlRootElement(name="url-mapping")
@XmlAccessorType(XmlAccessType.NONE)
public class URLMappingContainer
{
    /**
     * Name of this map.
     */
    @XmlAttribute
    private String name;

    /**
     * The list of map entries
     */
    private CopyOnWriteArrayList<URLMapEntry> urlMapEntries = new CopyOnWriteArrayList<>();

    /**
     * Set the name of this map.
     *
     * @param name
     *            Name of this map.
     */
    protected void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of this map.
     *
     * @return String Name of this map.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the list of map entries
     */
    @XmlElementWrapper(name="maps")
    @XmlElement(name="map")
    public List<URLMapEntry> getMapEntries()
    {
        return urlMapEntries;
    }

    /**
     * Set new map entries during deserialization
     *
     * @param newURLMapEntries the newURLMapEntries to set
     */
    protected void setMapEntries(List<URLMapEntry> newURLMapEntries)
    {
        this.urlMapEntries = new CopyOnWriteArrayList<URLMapEntry>(newURLMapEntries);
    }
}
