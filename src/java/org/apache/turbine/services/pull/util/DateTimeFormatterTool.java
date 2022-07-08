package org.apache.turbine.services.pull.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.localization.DateTimeFormatterInterface;
import org.apache.turbine.services.localization.DateTimeFormatterService;
import org.apache.turbine.services.pull.ApplicationTool;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

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
        implements ApplicationTool, DateTimeFormatterInterface
{

    @TurbineService
    private DateTimeFormatterService dtfs;

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
     * the {@link #defaultFormat} from {@link #dateTimeFormatPattern} with default Locale {@link Locale#getDefault()} and
     * Default zone: {@link ZoneId#systemDefault()}
     *
     * @param data initialization data
     */
    @Override
    public void init(Object data)
    {
        log.info("Initialized DateTimeFormatterTool with service {}",
                dtfs);
        if (dtfs == null)
        {
            ServiceManager serviceManager = TurbineServices.getInstance();
            dtfs = (DateTimeFormatterService)serviceManager.getService(DateTimeFormatterService.SERVICE_NAME);
        }
        // dtfs should be already initialized
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

    public DateTimeFormatter getDefaultFormat()
    {
        return getDtfs().getDefaultFormat();
    }

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
    public <T extends TemporalAccessor> String format(T temporalAccessor)
    {
        return getDtfs().getDefaultFormat().format(temporalAccessor);
    }

    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString)
    {
        return getDtfs().format(temporalAccessor, dateFormatString, null);
    }

    public <T extends TemporalAccessor> String format(T temporalAccessor, String dateFormatString, Locale locale)
    {
        return getDtfs().format(temporalAccessor, dateFormatString, locale);
    }
    
    public String map( String src, String outgoingFormatPattern, Locale locale, String incomingFormatPattern)
    {
        return getDtfs().map(src, outgoingFormatPattern, locale, incomingFormatPattern);
    }

    public String map( String src, java.time.format.DateTimeFormatter outgoingFormat, Locale locale, 
            java.time.format.DateTimeFormatter incomingFormat)
    {
        return getDtfs().map(src, outgoingFormat, locale, incomingFormat);
    }
    
    public String mapTo( String src, DateTimeFormatter outgoingFormat )
    {
        return  getDtfs().map( src, outgoingFormat, null, getDtfs().getDefaultFormat() );
    }

    public String mapFrom( String src, DateTimeFormatter incomingFormat )
    {
        return  getDtfs().map( src, getDtfs().getDefaultFormat(), null, incomingFormat );
    }

    public String map( String src,  DateTimeFormatter outgoingFormat, Locale locale )
    {
        return  getDtfs().map( src, outgoingFormat, locale, getDtfs().getDefaultFormat() );
    }
}
