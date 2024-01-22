package org.apache.turbine.services.localization;

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
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public interface DateTimeFormatterInterface {

    /**
     * Property tag for the date format that is to be used for the web
     * application. "tool.dateTool.format"
     */
    final String DATE_TIME_FORMAT_KEY = "datetime.format";
    
    final String DATE_TIME_ZONEID_KEY = "datetime.zoneId";
    
    final String USE_TURBINE_LOCALE_KEY = "datetime.use.turbine.locale";
    
    final String USE_REQUEST_LOCALE_KEY = "tool.use.request.locale";
    
    /** Default date format. find supported formats in {@link DateTimeFormatterService} */
    final String DATE_TIME_FORMAT_DEFAULT = "MM/dd/yyyy";

    DateTimeFormatter getDefaultFormat();

    String getFormatPattern();

    /**
     * Formats the given datetime as a String with the #{@link DateTimeFormatterService#getDefaultFormat()}.
     * using the default date format.
     *
     * @param temporalAccessor {@link TemporalAccessor to format
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor);

    /**
     * Formats the given date as a String.
     *
     * @param temporalAccessor TimeDate date to format
     * @param dateFormatString format string to use.  See {@link DateTimeFormatter}
     * for details.
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString);

    /**
     * Formats the given date as a String.
     *
     * @param temporalAccessor TimeDate date to format
     * @param dateFormatString format string to use.  See {@link DateTimeFormatter}
     * for details.
     * @param locale used to loclize the format
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale);
    
    /**
     * Formats the given date as a String.
     *
     * @param temporalAccessor TimeDate date to format
     * @param dateFormatString format string to use.  See {@link DateTimeFormatter}
     * for details.
     * @param locale the {@link Locale}
     * @param zoneId the {@link ZoneId}
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale, ZoneId zoneId);

    /**
     * Maps from an incoming format to an outgoing format {@link DateTimeFormatter}.
     * @param src the formatted datetime
     * @param outgoingFormat the outgoingFormat pattern, {@link DateTimeFormatter}
     * @param locale  Locale, if needed for outgoing formatting, no default.
     * @param incomingFormat the incming format pattern {@link DateTimeFormatter}, optional, default is {@link #getDefaultFormat()}.
     * @return the newly mapped
     */
    String map(String src, String outgoingFormat, Locale locale, String incomingFormat);

    /**
     * Uses as incoming format {@link #getDefaultFormat()} and no locale.
     * @param src the text, which will be parsed using the incomingFormat.
     * @param outgoingFormat the outgoing formatter, which will format.
     * @param locale The locale, if not null which will outgoingFormat use, {@link DateTimeFormatter#withLocale(Locale)}.
     * @param incomingFormat the incoming formatter, which will be parsed.
     * @return the formatted string
     *
     */
    String map(String src, DateTimeFormatter outgoingFormat, Locale locale,
               DateTimeFormatter incomingFormat);

    /**
     * Uses as outgoing {@link DateTimeFormatter} {@link #getDefaultFormat()} and no locale.
     *
     * @param src the datetime formatted string
     * @param outgoingFormat the format of this string
     * @return the date time formatted
     */
    String mapTo(String src, DateTimeFormatter outgoingFormat);

    /**
     * @param src the datetime formatted string
     * @param incomingFormat the format to which this incoming string should be formatted.
     *
     * @return the newly formatted date time string
     */
    String mapFrom(String src, DateTimeFormatter incomingFormat);

    /**
     * Uses as incoming {@link DateTimeFormatter}  {@link #getDefaultFormat()}.
     *
     * @param src the datetime formatted string
     * @param outgoingFormat the format to which this string should be formatted.
     * @param locale The locale, if not null,the incomingFormat will use.
     *
     * @return the newly formatted date time string
     */
    String map(String src, DateTimeFormatter outgoingFormat, Locale locale);

    ZoneId getZoneId();
}
