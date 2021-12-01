package org.apache.turbine.services.pull.util;


import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.pull.ApplicationTool;

/**
 * This pull tool is used to format {@link TemporalAccessor} and
 * {@link #map(String, DateTimeFormatter, Locale)} (different falvors)
 * objects into strings.
 *
 * The methods may throw {@link java.time.temporal.UnsupportedTemporalTypeException} or
 * {@link DateTimeParseException}.
 * if the source and the target format do not match appropriately.
 *
 */
public class DateTimeFormatterTool
        implements ApplicationTool
{
    /** Default date format. find supporrted formats in {@link DateTimeFormatterTool} */
    private static final String DATE_TIME_FORMAT_DEFAULT = "MM/dd/yyyy";

    /**
     * Property tag for the date format that is to be used for the web
     * application.
     */
    private static final String DATE_TIME_FORMAT_KEY = "tool.datetimeTool.format";

    private String dateFormat = null;
    
    private java.time.format.DateTimeFormatter defaultFormat = null;

    public java.time.format.DateTimeFormatter getDefaultFormat()
    {
        return defaultFormat;
    }

    private static final Logger log = LogManager.getLogger(DateTimeFormatterTool.class);

    /**
     * Initialize the application tool. The data parameter holds a different
     * type depending on how the tool is being instantiated:
     * <ul>
     * <li>For global tools data will be null</li>
     * <li>For request tools data will be of type RunData</li>
     * <li>For session and persistent tools data will be of type User</li>
     * </ul>
     *
     * the {@link #defaultFormat} from {@link #dateFormat} with default Locale {@link Locale#getDefault()} and
     * Default zone: {@link ZoneId#systemDefault()}
     *
     * @param data initialization data
     */
    @Override
    public void init(Object data)
    {
        dateFormat = Turbine.getConfiguration()
                .getString(DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_DEFAULT);
        defaultFormat = DateTimeFormatter.ofPattern(dateFormat)
                .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

        log.info("Initialized DateTimeFormatterTool with pattern {}, locale {} and zone {}",
                dateFormat, defaultFormat.getLocale(), defaultFormat.getZone());
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
    
    /**
     * Formats the given datetime as a String with the #{@link DateTimeFormatterTool#defaultFormat}.
     * using the default date format.
     *
     * @param the {@link TemporalAccessor to format
     * @return String value of the date
     */
    public <T extends TemporalAccessor> String format(T temporalAccessor)
    {
        return defaultFormat.format(temporalAccessor);
    }

    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString)
    {
        return format(temporalAccessor, dateFormatString, null);
    }
    /**
     * Formats the given date as a String.
     *
     * @param the TimeDate date to format
     * @param dateFormatString format string to use.  See java.text.SimpleDateFormat
     * for details.
     * @return String value of the date
     */
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale)
    {
        String result = null;

        if (StringUtils.isEmpty(dateFormatString) || temporalAccessor == null)
        {
            result = "";
        }
        else
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormatString);
            if (locale != null)
            {
                dtf.withLocale(locale);
            }
            result = dtf.format(temporalAccessor);
        }
        return result;
    }
    
    /**
     * Maps from an incoming format to an outgoing format {@link java.time.format.DateTimeFormatter}.
     * @param src the formatted datetime
     * @param outgoingFormat {@link java.time.format.DateTimeFormatter}
     * @param locale  Locale, if needed for outgoing formatting, no default.
     * @param incomingFormat {@link java.time.format.DateTimeFormatter}, optional, default is {@link #defaultFormat}.
     * @return the newly mapped
     */
    public String map( String src, java.time.format.DateTimeFormatter outgoingFormat, Locale locale, 
            java.time.format.DateTimeFormatter incomingFormat)
    {
        if (StringUtils.isEmpty(src) || outgoingFormat == null)
        {
            return "";
        }
        if (incomingFormat == null)
        {
            incomingFormat = defaultFormat;
        }
        if (incomingFormat.equals( outgoingFormat )) {
            return "";
        }
        if (locale != null)
        {
            outgoingFormat = outgoingFormat.withLocale( locale );
            //incomingFormat = incomingFormat.withLocale( locale );
        }
        return  outgoingFormat.format(
                incomingFormat.parse( src ));
    }

    /**
     * Uses as incoming format {@link #defaultFormat} and no locale.
     * @param src
     * @param outgoingFormat
     * @return the formatted string
     *
     * @throws java.time.temporal.UnsupportedTemporalTypeException
     */
    public String mapTo( String src, DateTimeFormatter outgoingFormat )
    {
        return map( src, outgoingFormat, null, defaultFormat );
    }

    /**
     * Uses as outgoing {@link DateTimeFormatter} {@link #defaultFormat} and no locale.
     * @param src the datetime formatted string
     * @param incomingFormat the format of this string
     * @return the date time formatted using the {@link #defaultFormat}.
     */
    public String mapFrom( String src, DateTimeFormatter incomingFormat )
    {
        return map( src, defaultFormat, null, incomingFormat );
    }

    /**
     * Uses as incoming {@link DateTimeFormatter}  {@link #defaultFormat}.
     * @param src the datetime formatted string
     * @param outgoingFormat the format to which this string should be formatted.
     * @param locale
     * @return the newly formatted date time string
     *
     * @throws java.time.temporal.UnsupportedTemporalTypeException
     */
    public String map( String src,  DateTimeFormatter outgoingFormat, Locale locale )
    {
        return map( src, outgoingFormat, locale, defaultFormat );
    }
}
