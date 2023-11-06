package org.apache.turbine.services.pull.util;

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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import org.apache.fulcrum.localization.LocalizationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.localization.DateTimeFormatterInterface;
import org.apache.turbine.services.localization.DateTimeFormatterService;
import org.apache.turbine.services.localization.RundataLocalizationService;
import org.apache.turbine.util.RunData;

/**
 * This pull tool is used to format {@link TemporalAccessor} and
 * {@link #map(String, DateTimeFormatter, Locale)} (different flavors)
 * objects into strings.
 * 
 * This tool extends {@link DateFormatter} to simplify configuration 
 * and to allow legacy {@link Date} inputs.
 *
 * The methods may throw {@link java.time.temporal.UnsupportedTemporalTypeException} or
 * {@link DateTimeParseException}.
 * if the source and the target format do not match appropriately.
 *
 */
public class DateTimeFormatterTool extends DateFormatter
        implements DateTimeFormatterInterface
{

    @TurbineService
    private DateTimeFormatterService dtfs;

    private static final Logger log = LogManager.getLogger(DateTimeFormatterTool.class);

    /** Fulcrum Localization component */
    @TurbineService
    private LocalizationService localizationService;
    
    protected Locale locale;
    
    private boolean overrideFromRequestLocale = false;
    
    /**
     * Initialize the application tool. The data parameter holds a different
     * type depending on how the tool is being instantiated:
     * <ul>
     * <li>For global tools data will be null</li>
     * <li>For request tools data will be of type RunData</li>
     * <li>For session and persistent tools data will be of type User</li>
     * </ul>
     *
     * the {@link #defaultFormat} from {@link #dateTimeFormatPattern} 
     * with {@link DateTimeFormatterService#getLocale()}
     * and zoneId {@link DateTimeFormatterService#getZoneId()} is used.
     * 
     * Customizations:
     * Locale could be fetched from request, if #USE_REQUEST_LOCALE_KEY is set to 
     * <code>true</code> (by default it is <code>false</code>.Then it will be retrieved either from 
     * {@link RundataLocalizationService#getLocale(RunData)} (if set in urbien role configuration)
     * or {@link LocalizationService#getLocale(javax.servlet.http.HttpServletRequest)}.
     * 
     * @param data initialization data
     */
    @Override
    public void init(Object data)
    {
        log.info("Initializing DateTimeFormatterTool with service {}",
                dtfs);
        if (dtfs == null)
        {
            ServiceManager serviceManager = TurbineServices.getInstance();
            dtfs = (DateTimeFormatterService)serviceManager.getService(DateTimeFormatterService.SERVICE_NAME);
        }
        
        overrideFromRequestLocale = Turbine.getConfiguration()
                .getBoolean(USE_REQUEST_LOCALE_KEY, false);
        // dtfs should be already initialized
        if (overrideFromRequestLocale && data instanceof RunData)
        {
            // Pull necessary information out of RunData while we have
            // a reference to it.
            locale = (localizationService instanceof RundataLocalizationService)?
                    ((RundataLocalizationService)localizationService).getLocale((RunData) data):
                    localizationService.getLocale(((RunData) data).getRequest());
            log.info("Override {} with request locale {}.", dtfs.getLocale(), locale);
        }
    }

    public DateTimeFormatterService getDtfs() {
        return dtfs;
    }

    /**
     * Refresh the application tool. This is
     * necessary for development work where you
     * probably want the tool to refresh itself
     * if it is using configuration information
     * that is typically cached after initialization
     */
    @Override
    public void refresh()
    {
        // empty
    }

    @Override
    public DateTimeFormatter getDefaultFormat()
    {
        return getDtfs().getDefaultFormat();
    }

    @Override
    public String getDateTimeFormatPattern() {
        return getDtfs().getDateTimeFormatPattern();
    }

    /**
     * Formats the given datetime as a String with the #{@link DateTimeFormatterTool#defaultFormat}.
     * using the default date format.
     *
     * @param the {@link TemporalAccessor to format
     * @return String value of the date
     */
    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor)
    {
        return getDtfs().getDefaultFormat().format(temporalAccessor);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString)
    {
        return getDtfs().format(temporalAccessor, dateFormatString);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale)
    {
        return getDtfs().format(temporalAccessor, dateFormatString, locale);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale,
            ZoneId zoneId) {
        return getDtfs().format(temporalAccessor, dateFormatString, locale, zoneId);
    }

    @Override
    public String map( String src, String outgoingFormatPattern, Locale locale, String incomingFormatPattern)
    {
        return getDtfs().map(src, outgoingFormatPattern, locale, incomingFormatPattern);
    }

    @Override
    public String map( String src, java.time.format.DateTimeFormatter outgoingFormat, 
            Locale locale, java.time.format.DateTimeFormatter incomingFormat)
    {
        return getDtfs().map(src, outgoingFormat, locale, incomingFormat);
    }

    @Override
    public String mapTo( String src, DateTimeFormatter outgoingFormat )
    {
        return  getDtfs().map( src, outgoingFormat, getLocale(), getDtfs().getDefaultFormat() );
    }

    @Override
    public String mapFrom( String src, DateTimeFormatter incomingFormat )
    {
        return  getDtfs().map( src, getDtfs().getDefaultFormat(), getLocale(), incomingFormat );
    }

    @Override
    public String map( String src,  DateTimeFormatter outgoingFormat, Locale locale )
    {
        return  getDtfs().map( src, outgoingFormat, locale, getDtfs().getDefaultFormat() );
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
