package org.apache.turbine.services.urlmapper.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.turbine.services.urlmapper.model.XmlParameterList.XmlParameter;

/**
 * Creates Map objects from XmlParameterList objects and vice-versa.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class XmlParameterAdapter extends XmlAdapter<XmlParameterList, Map<String, String>>
{
    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(XmlParameterList xmlList) throws Exception
    {
        // Make sure that order is kept
        return xmlList.getXmlParameters().stream()
                .collect(Collectors.toMap(xml -> xml.key, xml -> xml.value,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public XmlParameterList marshal(Map<String, String> map) throws Exception
    {
        XmlParameterList xmlList = new XmlParameterList();
        xmlList.setXmlParameters(map.entrySet().stream()
                .map(entry -> new XmlParameter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        return xmlList;
    }
}
