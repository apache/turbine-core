package org.apache.turbine.services.localization;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.LocaleUtils;

/**
 * This service is used to format {@link TemporalAccessor} and
 * {@link #map(String, DateTimeFormatter, Locale)} (different flavors)
 * objects into strings.
 *
 * The methods may throw {@link java.time.temporal.UnsupportedTemporalTypeException} or
 * {@link DateTimeParseException}, e.g.
 * if the source and the target format do not match appropriately.
 *
 */
@TurbineService("DateTimeFormatterService")
public class DateTimeFormatterService
        extends TurbineBaseService implements DateTimeFormatterInterface 
{

    private String formatPattern = null;

    private DateTimeFormatter dateTimeFormat = null;

    private Locale locale = null;

    private ZoneId zoneId;

    @Override
    public DateTimeFormatter getDefaultFormat()
    {
        return dateTimeFormat;
    }

    @Override
    public String getFormatPattern() {
        return formatPattern;
    }

    private static final Logger log = LogManager.getLogger(DateTimeFormatterService.class);

    /**
     * Initialize the service.
     *
     * the {@link #dateTimeFormat} from {@link #formatPattern} is initialized with
     * 
     * <ol>
     * <li>{@link Locale}: {@link LocaleUtils#getDefaultLocale()} is used by default.
     * It could be overridden setting #USE_TURBINE_LOCALE_KEY to false, the
     * the default Locale {@link Locale#getDefault()} is used.
     * </li><li>{@link ZoneId}: If #DATE_TIME_ZONEID_KEY is set this {@link ZoneId} 
     * is used else {@link ZoneId#systemDefault()}.
     * </li>
     * </ol>
     */
    @Override
    public void init()
    {
        formatPattern = Turbine.getConfiguration()
                .getString(DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_DEFAULT);

        boolean useTurbineLocale =  Turbine.getConfiguration()
        .getBoolean(USE_TURBINE_LOCALE_KEY, true);

        Locale localeSetter = (useTurbineLocale && LocaleUtils.getDefaultLocale() != null)?
                LocaleUtils.getDefaultLocale()
                : Locale.getDefault();
        setLocale(localeSetter);

        String zoneIdStr = Turbine.getConfiguration()
        .getString(DATE_TIME_ZONEID_KEY);
        ZoneId zoneIdSet = (zoneIdStr != null)?  ZoneId.of( zoneIdStr ) :
            ZoneId.systemDefault();
         setZoneId(zoneIdSet);

        dateTimeFormat = DateTimeFormatter.ofPattern(formatPattern)
                .withLocale(localeSetter).withZone(zoneIdSet);

        log.info("Initialized DateTimeFormatterService with pattern {}, locale {} and zone {}",
                formatPattern, dateTimeFormat.getLocale(),
                dateTimeFormat.getZone());
        setInit(true);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor)
    {
        return dateTimeFormat.format(temporalAccessor);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString)
    {
        return format(temporalAccessor, dateFormatString, null, null);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale)
    {
        return format(temporalAccessor, dateFormatString, locale, null);
    }

    @Override
    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale,
            ZoneId zoneId) {
        String result = null;

        if (StringUtils.isEmpty(dateFormatString) || temporalAccessor == null)
        {
            result = "";
        }
        else
        {
            DateTimeFormatter dtf =
                    DateTimeFormatter.ofPattern(dateFormatString);
            if (locale != null)
            {
                dtf = dtf.withLocale(locale);
            } else {
                log.warn("adding default local {}",  getLocale() );
                dtf = dtf.withLocale( getLocale());
            }
            if (zoneId != null)
            {
                dtf = dtf.withZone(zoneId);
            } else {
                log.warn("adding default zone {}", getZoneId() );
                dtf = dtf.withZone(getZoneId());
            }
            log.warn("try to format {} with {}.", temporalAccessor, dtf );
            try {
                result =
                        dtf.format(temporalAccessor);
            } catch(DateTimeException e) {
                log.error("An exception with date time formatting was thrown: {}", e);
                // check with dtf.toFormat().format(temporalAccessor)?
                throw e;
            }
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
            incomingFormatPattern = formatPattern;
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
            incomingFormat = dateTimeFormat;
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
        return map( src, outgoingFormat, null, dateTimeFormat );
    }

    @Override
    public String mapFrom(String src, DateTimeFormatter incomingFormat)
    {
        return map( src, dateTimeFormat, null, incomingFormat );
    }

    @Override
    public String map(String src, DateTimeFormatter outgoingFormat, Locale locale)
    {
        return map( src, outgoingFormat, locale, dateTimeFormat );
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

}
