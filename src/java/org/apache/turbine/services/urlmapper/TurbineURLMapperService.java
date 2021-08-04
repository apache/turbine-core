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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * The URL mapper service provides methods to map a set of parameters to a
 * simplified URL and vice-versa. This service was inspired by the
 * Liferay Friendly URL Mapper.
 * <p>
 * A mapper valve and a link pull tool are provided for easy application.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @see URLMapperService
 * @see MappedTemplateLink
 * @see URLMapperValve
 *
 * @version $Id$
 */
public class TurbineURLMapperService
        extends TurbineBaseService
        implements URLMapperService
{
    /**
     * Logging.
     */
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
     * The container with the URL mappings.
     */
    private URLMappingContainer container;

    /**
     * Regex pattern for group names, equivalent to the characters defined in java {@link Pattern} (private) groupname method.
     */
    private static final Pattern NAMED_GROUPS_PATTERN = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>.+?\\)");

    /**
     * Regex pattern for multiple slashes
     */
    private static final Pattern MULTI_SLASH_PATTERN = Pattern.compile("[/]+");

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
    private static final Set<String> DEFAULT_PARAMETERS = new HashSet<>(Arrays.asList(
            CONTEXT_PATH_PARAMETER,
            WEBAPP_ROOT_PARAMETER
    ));

    /**
     * Map a set of parameters (contained in TurbineURI PathInfo and QueryData)
     * to a TurbineURI
     *
     * @param uri the URI to be modified (with setScriptName())
     */
    @Override
    public void mapToURL(TurbineURI uri)
    {
        if (!uri.hasPathInfo() && !uri.hasQueryData())
        {
            return; // no mapping or mapping already done
        }

        List<URIParam> pathInfo = uri.getPathInfo();
        List<URIParam> queryData = uri.getQueryData();

        // Create map from list, taking only the first appearance of a key
        // PathInfo takes precedence
        Map<String, Object> uriParameterMap =
                Stream.concat(pathInfo.stream(), queryData.stream())
                    .collect(Collectors.toMap(
                        URIParam::getKey,
                        URIParam::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        for (URLMapEntry urlMap : container.getMapEntries())
        {
            Set<String> keys = new HashSet<>(uriParameterMap.keySet());
            keys.removeAll(urlMap.getIgnoreParameters().keySet());

            Set<String> entryKeys = new HashSet<>(urlMap.getGroupNamesMap().keySet());

            Set<String> implicitKeysFound = urlMap.getImplicitParameters().entrySet().stream()
                    .filter(entry -> Objects.equals(uriParameterMap.get(entry.getKey()), entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            entryKeys.addAll(implicitKeysFound);

            if (entryKeys.containsAll(keys))
            {
                Matcher matcher = NAMED_GROUPS_PATTERN.matcher(urlMap.getUrlPattern().pattern());
                StringBuffer sb = new StringBuffer();

                while (matcher.find())
                {
                    String key = matcher.group(1);

                    if (CONTEXT_PATH_PARAMETER.equals(key))
                    {
                        // remove
                        matcher.appendReplacement(sb, "");
                    } else if (WEBAPP_ROOT_PARAMETER.equals(key))
                    {
                        matcher.appendReplacement(sb, uri.getScriptName());
                    } else
                    {
                        boolean ignore = urlMap.getIgnoreParameters().keySet().stream()
                                .anyMatch( x-> x.equals( key ) );
                        matcher.appendReplacement(sb,
                                 Matcher.quoteReplacement(
                                        (!ignore)? Objects.toString(uriParameterMap.get(key)):""));
                        // Remove handled parameters (all of them!)
                        pathInfo.removeIf(uriParam -> key.equals(uriParam.getKey()));
                        queryData.removeIf(uriParam -> key.equals(uriParam.getKey()));
                    }
                }

                matcher.appendTail(sb);
                
                implicitKeysFound.forEach(key -> {
                    pathInfo.removeIf(uriParam -> key.equals(uriParam.getKey()));
                    queryData.removeIf(uriParam -> key.equals(uriParam.getKey()));
                });

                // Clean up
                uri.setScriptName(MULTI_SLASH_PATTERN.matcher(sb).replaceAll("/").replaceFirst( "/$", "" ));
                
                break;
            }
        }
        
        log.debug("mapped to uri: {} ", uri);
    }

    /**
     * Map a simplified URL to a set of parameters
     *
     * @param url the URL
     * @param pp  a ParameterParser to use for parameter mangling
     */
    @Override
    public void mapFromURL(String url, ParameterParser pp)
    {
        for (URLMapEntry urlMap : container.getMapEntries())
        {
            url = url.replaceFirst( "/$", "" );
            Matcher matcher = urlMap.getUrlPattern().matcher(url);
            if (matcher.matches())
            {
                // extract parameters from URL
                urlMap.getGroupNamesMap().entrySet().stream()
                        // ignore default parameters
                        .filter(group -> !DEFAULT_PARAMETERS.contains(group.getKey()))
                        .forEach(group ->
                                pp.setString(group.getKey(), matcher.group(group.getValue().intValue())));

                // add implicit parameters
                urlMap.getImplicitParameters().entrySet().forEach(e ->
                        pp.add(e.getKey(), e.getValue()));

                // add override parameters
                urlMap.getOverrideParameters().entrySet().forEach(e ->
                        pp.setString(e.getKey(), e.getValue()));

                // remove ignore parameters
                urlMap.getIgnoreParameters().keySet().forEach(k ->
                        pp.remove(k));
                
                log.debug("mapped {} params from url {} ", pp.getKeys().length, url);

                break;
            }
        }
    }

    // ---- Service initialization ------------------------------------------

    /**
     * Initializes the service.
     */
    @Override
    public void init() throws InitializationException
    {
        Configuration cfg = getConfiguration();

        String configFile = cfg.getString(CONFIGURATION_FILE_KEY, DEFAULT_CONFIGURATION_FILE);

        // context resource path has to begin with slash, cft.
        // context.getResource
        if (!configFile.startsWith("/"))
        {
            configFile = "/" + configFile;
        }

        ServletService servletService = (ServletService) TurbineServices.getInstance().getService(ServletService.SERVICE_NAME);

        try (InputStream reader = servletService.getResourceAsStream(configFile))
        {
            if (configFile.endsWith(".xml"))
            {
                JAXBContext jaxb = JAXBContext.newInstance(URLMappingContainer.class);
                Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                container = (URLMappingContainer) unmarshaller.unmarshal(reader);
            } else if (configFile.endsWith(".yml"))
            {
                // org.apache.commons.configuration2.YAMLConfiguration does only expose property like configuration values,
                // which is not what we need here -> java object deserialization.
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                container = mapper.readValue(reader, URLMappingContainer.class);
            } else if (configFile.endsWith(".json"))
            {
                ObjectMapper mapper = JsonMapper.builder().build();
                container = mapper.readValue(reader, URLMappingContainer.class);
            }
        }
        catch (IOException | JAXBException e)
        {
            throw new InitializationException("Could not load configuration file " + configFile, e);
        }

        // Get groupNamesMap for every Pattern and store it in the entry
        for (URLMapEntry urlMap : container.getMapEntries())
        {
            int position = 1;
            Map<String, Integer> groupNamesMap = new HashMap<>();
            Matcher matcher = NAMED_GROUPS_PATTERN.matcher(urlMap.getUrlPattern().pattern());

            while (matcher.find())
            {
                groupNamesMap.put(matcher.group(1), Integer.valueOf(position++));
            }
            urlMap.setGroupNamesMap(groupNamesMap);
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
        container.getMapEntries().clear();
        setInit(false);
    }
}
