package org.apache.turbine.services.localization;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public interface DateTimeFormatterInterface {

    /** Default date format. find supporrted formats in {@link DateTimeFormatterService} */
    final String DATE_TIME_FORMAT_DEFAULT = "MM/dd/yyyy";

    /**
     * Property tag for the date format that is to be used for the web
     * application.
     */
    final String DATE_TIME_FORMAT_KEY = "tool.datetimeTool.format";

    DateTimeFormatter getDefaultFormat();

    String getDateTimeFormatPattern();

    /**
     * Formats the given datetime as a String with the #{@link DateTimeFormatterService#defaultFormat}.
     * using the default date format.
     *
     * @param the {@link TemporalAccessor to format
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor);

    /**
     * Formats the given date as a String.
     *
     * @param the TimeDate date to format
     * @param dateFormatString format string to use.  See {@link DateTimeFormatter}
     * for details.
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString);

    /**
     * Formats the given date as a String.
     *
     * @param the TimeDate date to format
     * @param dateFormatString format string to use.  See {@link DateTimeFormatter}
     * for details.
     * @param locale
     * @return String value of the date
     */
    <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale);

    /**
     * Maps from an incoming format to an outgoing format {@link DateTimeFormatter}.
     * @param src the formatted datetime
     * @param outgoingFormat {@link DateTimeFormatter}
     * @param locale  Locale, if needed for outgoing formatting, no default.
     * @param incomingFormat {@link DateTimeFormatter}, optional, default is {@link #defaultFormat}.
     * @return the newly mapped
     */
    String map(String src, String outgoingFormatPattern, Locale locale, String incomingFormatPattern);

    /**
     * Uses as incoming format {@link #defaultFormat} and no locale.
     * @param src
     * @param outgoingFormat
     * @return the formatted string
     *
     * @throws java.time.temporal.UnsupportedTemporalTypeException
     */
    String map(String src, DateTimeFormatter outgoingFormat, Locale locale,
               DateTimeFormatter incomingFormat);

    /**
     * Uses as outgoing {@link DateTimeFormatter} {@link #defaultFormat} and no locale.
     * @param src the datetime formatted string
     * @param incomingFormat the format of this string
     * @return the date time formatted using the {@link #defaultFormat}.
     */
    String mapTo(String src, DateTimeFormatter outgoingFormat);

    /**
     * Uses as incoming {@link DateTimeFormatter}  {@link #defaultFormat}.
     * @param src the datetime formatted string
     * @param outgoingFormat the format to which this string should be formatted.
     * @param locale
     * @return the newly formatted date time string
     *
     * @throws java.time.temporal.UnsupportedTemporalTypeException
     */
    String mapFrom(String src, DateTimeFormatter incomingFormat);

    String map(String src, DateTimeFormatter outgoingFormat, Locale locale);
}
