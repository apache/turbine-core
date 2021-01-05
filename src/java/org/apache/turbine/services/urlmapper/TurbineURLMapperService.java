package org.apache.turbine.services.urlmapper;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.configuration2.Configuration;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.servlet.ServletService;
import org.apache.turbine.services.urlmapper.model.URLMapEntry;
import org.apache.turbine.services.urlmapper.model.URLMappingContainer;
import org.apache.turbine.util.uri.TurbineURI;
import org.apache.turbine.util.uri.URIParam;

/**
 * The URL mapper service provides methods to map a set of parameters to a
 * simplified URL and vice-versa. This service was inspired by the
 * Liferay Friendly URL Mapper.
 *
 * A mapper valve and a link pull tool are provided for easy application.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 *
 * @see URLMapperService
 * @see URLMapperTemplateLink
 * @see URLMapperContentLink
 * @see URLMapperValve
 */
public class TurbineURLMapperService
        extends TurbineBaseService
        implements URLMapperService
{
    /** Logging. */
    private static final Logger log = LogManager.getLogger(TurbineURLMapperService.class);

    /**
     * The default configuration file.
     */
    private static final String DEFAULT_CONFIGURATION_FILE = "/WEB-INF/conf/turbine-url-mapping.xml";

    /**
     * The configuration key for the configuration file.
     */
    private static final String CONFIGURATION_FILE_KEY = "configFile";

    /**
     * The configuration file.
     */
    private String configFile;

    /**
     * The container with the URL mappings.
     */
    private URLMappingContainer container;

    /**
     * Non-public method to read the names of regex groups from a Pattern
     */
    private Method namedGroupsMethod;

    /**
     * Regex pattern for group names
     */
    private static final Pattern namedGroupsPattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>.+?\\)");

    /**
     * Symbolic group name for context path
     */
    private static final String CONTEXT_PATH_PARAMETER = "contextPath";

    /**
     * Symbolic group name for web application root
     */
    private static final String WEBAPP_ROOT_PARAMETER = "webAppRoot";

    /**
     * Symbolic group names that will not be added to parameters
     */
    private static final Set<String> DEFAULT_PARAMETERS = Stream.of(
            CONTEXT_PATH_PARAMETER,
            WEBAPP_ROOT_PARAMETER
            ).collect(Collectors.toSet());

    /**
     * Map a set of parameters (contained in TurbineURI PathInfo and QueryData)
     * to a TurbineURI
     *
     * @param uri the URI to be modified (with setScriptName())
     */
    @Override
    public void mapToURL(TurbineURI uri)
    {
        // Create map from list, taking only the first appearance of a key
        // PathInfo takes precedence
        Map<String, Object> uriParameterMap = Stream.concat(
                uri.getPathInfo().stream(),
                uri.getQueryData().stream())
                .collect(Collectors.toMap(
                        URIParam::getKey,
                        URIParam::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        Set<String> keys = new HashSet<>(uriParameterMap.keySet());

        for (URLMapEntry urlMap : container.getMapEntries())
        {
            Set<String> entryKeys = new HashSet<>();

            Map<String, Integer> groupNamesMap = urlMap.getGroupNamesMap();
            if (groupNamesMap != null)
            {
                entryKeys.addAll(groupNamesMap.keySet());
            }

            Set<String> implicitKeysFound = urlMap.getImplicitParameters().entrySet().stream()
                    .filter(entry -> Objects.equals(uriParameterMap.get(entry.getKey()), entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            entryKeys.addAll(implicitKeysFound);
            implicitKeysFound.forEach(key -> {
                uri.removePathInfo(key);
                uri.removeQueryData(key);
            });

            keys.removeAll(urlMap.getIgnoreParameters().keySet());

            if (entryKeys.containsAll(keys))
            {
                Matcher matcher = namedGroupsPattern.matcher(urlMap.getUrlPattern().pattern());
                StringBuffer sb = new StringBuffer();

                while (matcher.find())
                {
                    String key = matcher.group(1);

                    if (CONTEXT_PATH_PARAMETER.equals(key))
                    {
                        // remove
                        matcher.appendReplacement(sb, "");
                    }
                    else if (WEBAPP_ROOT_PARAMETER.equals(key))
                    {
                        matcher.appendReplacement(sb, uri.getScriptName());
                    }
                    else
                    {
                        matcher.appendReplacement(sb,
                                Matcher.quoteReplacement(
                                        Objects.toString(uriParameterMap.get(key))));
                        // Remove handled parameters (all of them!)
                        uri.removePathInfo(key);
                        uri.removeQueryData(key);
                    }
                }

                matcher.appendTail(sb);
                // Clean up
                uri.setScriptName(sb.toString().replace("//", "/"));
                break;
            }
        }
    }

    /**
     * Map a simplified URL to a set of parameters
     *
     * @param url the URL
     * @param pp a ParameterParser to use for parameter mangling
     */
    @Override
    public void mapFromURL(String url, ParameterParser pp)
    {
        for (URLMapEntry urlMap : container.getMapEntries())
        {
            Matcher matcher = urlMap.getUrlPattern().matcher(url);
            if (matcher.matches())
            {
                // extract parameters from URL
                Map<String, Integer> groupNameMap = urlMap.getGroupNamesMap();

                if (groupNameMap != null)
                {
                    groupNameMap.entrySet().stream()
                        // ignore default parameters
                        .filter(group -> !DEFAULT_PARAMETERS.contains(group.getKey()))
                        .forEach(group ->
                            pp.setString(group.getKey(), matcher.group(group.getValue().intValue())));
                }

                // add implicit parameters
                urlMap.getImplicitParameters().entrySet().forEach(e ->
                    pp.add(e.getKey(), e.getValue()));

                // add override parameters
                urlMap.getOverrideParameters().entrySet().forEach(e ->
                    pp.setString(e.getKey(), e.getValue()));

                // remove ignore parameters
                urlMap.getIgnoreParameters().keySet().forEach(k ->
                    pp.remove(k));

                break;
            }
        }
    }

    /**
     * Get the named groups from a Pattern
     * This method uses reflection to call a non-public method of the
     * Pattern class
     *
     * @param regex the pattern
     * @return a Map of group names to group indices
     *
     * @throws InvocationTargetException  if the underlying method throws an
     *     exception.
     * @throws IllegalArgumentException if the method is an instance method
     *     and the specified object argument is not an instance of the class
     *     or interface declaring the underlying method (or of a subclass or
     *     implementor thereof); if the number of actual and formal parameters
     *     differ; if an unwrapping conversion for primitive arguments fails;
     *     or if, after possible unwrapping, a parameter value cannot be
     *     converted to the corresponding formal parameter type by a method
     *     invocation conversion.
     * @throws IllegalAccessException if this Method object is enforcing Java
     *     language access control and the underlying method is inaccessible.
     */
    private Map<String, Integer> getNamedGroups(Pattern regex)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        @SuppressWarnings("unchecked")
        Map<String, Integer> groupNamesMap = (Map<String, Integer>) namedGroupsMethod.invoke(regex);
        return groupNamesMap;
    }

    // ---- Service initialization ------------------------------------------

    /**
     * Initializes the service.
     */
    @Override
    public void init() throws InitializationException
    {
        try
        {
            namedGroupsMethod = Pattern.class.getDeclaredMethod("namedGroups");
            namedGroupsMethod.setAccessible(true);
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new InitializationException("Could not find method Pattern.getNamedGroups", e);
        }

        Configuration cfg = getConfiguration();

        ServletService servletService = (ServletService)TurbineServices.getInstance().getService(ServletService.SERVICE_NAME);

        configFile = cfg.getString(CONFIGURATION_FILE_KEY, DEFAULT_CONFIGURATION_FILE);

        // context resource path has to begin with slash, cft.
        // context.getResource
        if (!configFile.startsWith("/"))
        {
            configFile = "/" + configFile;
        }

        try (InputStream reader = servletService.getResourceAsStream(configFile))
        {
            JAXBContext jaxb = JAXBContext.newInstance(URLMappingContainer.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            container = (URLMappingContainer) unmarshaller.unmarshal(reader);
        }
        catch (IOException | JAXBException e)
        {
            throw new InitializationException("Could not load configuration file " + configFile, e);
        }

        // Get groupNamesMap for every Pattern and store it in the entry
        try
        {
            for (URLMapEntry urlMap : container.getMapEntries())
            {
                Map<String, Integer> groupNamesMap = getNamedGroups(urlMap.getUrlPattern());
                urlMap.setGroupNamesMap(groupNamesMap);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            throw new InitializationException("Could not invoke method Pattern.getNamedGroups", e);
        }

        log.info("Loaded {} url-mappings from {}", Integer.valueOf(container.getMapEntries().size()), configFile);

        setInit(true);
    }

    /**
     * Returns to uninitialized state.
     */
    @Override
    public void shutdown()
    {
        setInit(false);
    }
}
