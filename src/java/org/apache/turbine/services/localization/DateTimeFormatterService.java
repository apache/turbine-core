package org.apache.turbine.services.localization;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineBaseService;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * This service is used to format {@link TemporalAccessor} and
 * {@link #map(String, DateTimeFormatter, Locale)} (different falvors)
 * objects into strings.
 *
 * The methods may throw {@link java.time.temporal.UnsupportedTemporalTypeException} or
 * {@link DateTimeParseException}.
 * if the source and the target format do not match appropriately.
 *
 */
public class DateTimeFormatterService
        extends TurbineBaseService implements DateTimeFormatterInterface {

    public static final String SERVICE_NAME = "DateTimeFormatterService";

    public static final String ROLE = DateTimeFormatterService.class.getName();

    private String dateTimeFormatPattern = null;
    
    private DateTimeFormatter defaultFormat = null;

    @Override
    public DateTimeFormatter getDefaultFormat()
    {
        return defaultFormat;
    }

    @Override
    public String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    private static final Logger log = LogManager.getLogger(DateTimeFormatterService.class);

    /**
     * Initialize the service.
     *
     * the {@link #defaultFormat} from {@link #dateTimeFormatPattern} is initialized with
     * the default Locale {@link Locale#getDefault()} and default zone: {@link ZoneId#systemDefault()}.
     *
     */
    @Override
    public void init()
    {
        dateTimeFormatPattern = Turbine.getConfiguration()
                .getString(DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_DEFAULT);
        defaultFormat = DateTimeFormatter.ofPattern(dateTimeFormatPattern)
                .withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

        log.info("Initialized DateTimeFormatterService with pattern {}, locale {} and zone {}",
                dateTimeFormatPattern, defaultFormat.getLocale(), defaultFormat.getZone());
        setInit(true);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor)
    {
        return defaultFormat.format(temporalAccessor);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString)
    {
        return format(temporalAccessor, dateFormatString, null);
    }

    @Override
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
    
    @Override
    public String map(String src, String outgoingFormatPattern, Locale locale, String incomingFormatPattern)
    {
        if (StringUtils.isEmpty(src) || outgoingFormatPattern == null)
        {
            return "";
        }
        if (incomingFormatPattern == null)
        {
            incomingFormatPattern = dateTimeFormatPattern;
        }
        if (incomingFormatPattern.equals( outgoingFormatPattern )) {
            return "";
        }
        DateTimeFormatter incomingFormat = DateTimeFormatter.ofPattern(incomingFormatPattern);
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ofPattern(outgoingFormatPattern);
        if (locale != null)
        {
            outgoingFormat = outgoingFormat.withLocale( locale );
            //incomingFormat = incomingFormat.withLocale( locale );
        }
        return map( src, outgoingFormat, locale, incomingFormat );
    }

    @Override
    public String map(String src, DateTimeFormatter outgoingFormat, Locale locale,
                      DateTimeFormatter incomingFormat)
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

    @Override
    public String mapTo(String src, DateTimeFormatter outgoingFormat)
    {
        return map( src, outgoingFormat, null, defaultFormat );
    }

    @Override
    public String mapFrom(String src, DateTimeFormatter incomingFormat)
    {
        return map( src, defaultFormat, null, incomingFormat );
    }

    @Override
    public String map(String src, DateTimeFormatter outgoingFormat, Locale locale)
    {
        return map( src, outgoingFormat, locale, defaultFormat );
    }
}
