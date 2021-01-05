package org.apache.turbine.services.urlmapper.model;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * The url map model class
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@XmlType(name="map")
@XmlAccessorType(XmlAccessType.NONE)
public class URLMapEntry
{
    private Pattern urlPattern;
    private Map<String, String> implicit = new LinkedHashMap<>();
    private Map<String, String> ignore = new LinkedHashMap<>();
    private Map<String, String> override = new LinkedHashMap<>();

    private Map<String, Integer> groupNamesMap;
    private Set<String> relevantKeys = null;

    /**
     * @return the urlPattern
     */
    @XmlElement(name="pattern")
    @XmlJavaTypeAdapter(XmlPatternAdapter.class)
    public Pattern getUrlPattern()
    {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    protected void setUrlPattern(Pattern urlPattern)
    {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the implicit parameters
     */
    @XmlElement(name="implicit-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    public Map<String, String> getImplicitParameters()
    {
        return implicit;
    }

    /**
     * @param implicit the implicit parameters to set
     */
    protected void setImplicitParameters(Map<String, String> implicit)
    {
        this.implicit = implicit;
    }

    /**
     * @return the ignored parameters
     */
    @XmlElement(name="ignore-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    public Map<String, String> getIgnoreParameters()
    {
        return ignore;
    }

    /**
     * @param ignore the ignored parameters to set
     */
    protected void setIgnoreParameters(Map<String, String> ignore)
    {
        this.ignore = ignore;
    }

    /**
     * @return the override parameters
     */
    @XmlElement(name="override-parameters")
    @XmlJavaTypeAdapter(XmlParameterAdapter.class)
    public Map<String, String> getOverrideParameters()
    {
        return override;
    }

    /**
     * @param override the override parameters to set
     */
    protected void setOverrideParameters(Map<String, String> override)
    {
        this.override = override;
    }

    /**
     * Get the map of group names to group indices for the stored Pattern
     *
     * @return the groupNamesMap
     */
    public Map<String, Integer> getGroupNamesMap()
    {
        return groupNamesMap;
    }

    /**
     * Set the map of group names to group indices for the stored Pattern
     *
     * @param groupNamesMap the groupNamesMap to set
     */
    public void setGroupNamesMap(Map<String, Integer> groupNamesMap)
    {
        this.groupNamesMap = groupNamesMap;
    }

    /**
     * Get the set of relevant keys for comparison (cached for performance)
     *
     * @return the relevantKeys
     */
    public Set<String> getRelevantKeys()
    {
        return relevantKeys;
    }

    /**
     * Set the set of relevant keys for comparison (cached for performance)
     *
     * @param relevantKeys the relevantKeys to set
     */
    public void setRelevantKeys(Set<String> relevantKeys)
    {
        this.relevantKeys = relevantKeys;
    }
}
