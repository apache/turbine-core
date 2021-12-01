package org.apache.turbine.services.pull.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for DateTimeFormatter.
 *
 */
public class DateTimeFormatterTest
{

    private static DateTimeFormatterTool df;

    private static TurbineConfig tc = null;

    @BeforeAll
    public static void setup() 
    {
        // required to initialize defaults
        tc = new TurbineConfig(
                        ".",
                        "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
        df = new DateTimeFormatterTool();
        df.init(null);
    }

    @AfterAll
    public static void tearDown()
    {
        tc.dispose();
    }
    /*
     * Class under test for String format(Date, String)
     */
    @Test public void testFormatDateString()
    {
        LocalDateTime ldt = LocalDateTime.now();
        int day = ldt.get(ChronoField.DAY_OF_MONTH);
        int month = ldt.get(ChronoField.MONTH_OF_YEAR) ; // one based
        int year = ldt.get(ChronoField.YEAR);
        
        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String ddmmyyyy = dayString + "/" + monthString + "/" + year;
        assertEquals(ddmmyyyy, df.format(ldt, "dd/MM/yyyy"));

        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year;
        assertEquals(mmddyyyy, df.format(ldt, "MM/dd/yyyy"));
    }
    
    @Test public void testFormatZonedDateString()
    {  
        ZonedDateTime zdt = ZonedDateTime.now();
        int day = zdt.get(ChronoField.DAY_OF_MONTH);
        int month = zdt.get(ChronoField.MONTH_OF_YEAR) ; // one based
        int year = zdt.get(ChronoField.YEAR);
        zdt = zdt.truncatedTo( ChronoUnit.MINUTES );

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String ddmmyyyy = dayString + "/" + monthString + "/" + year;
        assertEquals(ddmmyyyy, df.format(zdt, "dd/MM/yyyy"));

        int hours = zdt.get(ChronoField.HOUR_OF_DAY);
        int mins =zdt.get(ChronoField.MINUTE_OF_HOUR);
        int secs = zdt.get(ChronoField.SECOND_OF_MINUTE);
        String hourString = (hours < 10 ? "0" : "") + hours;
        String minsString = (mins < 10 ? "0" : "") + mins;
        String secsString = (secs < 10 ? "0" : "") + secs;

        String zone = zdt.getZone().getId();
        String offset = zdt.getOffset().getId();
        // offset formatting not easy matchable, removed
        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year + " " + hourString + ":" + minsString + ":"+ secsString + " " + zone;
        // zone + offset format, removed offset ZZZ
        assertEquals(mmddyyyy, df.format(zdt, "MM/dd/yyyy HH:mm:ss VV"));
    }

    /*
     * Class under test for String mapFrom(String, DateTimeFormatter)
     */
    @Test public void testDefaultMapFromInstant()
    {
        DateTimeFormatter incomingFormat = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());
        // may throws an DateTimeParseException
        Instant now = Instant.now().truncatedTo( ChronoUnit.MINUTES );
        String source = incomingFormat.format(now);

        TemporalAccessor dateTimeFromInstant = incomingFormat.parse(source);
        int day = dateTimeFromInstant.get(ChronoField.DAY_OF_MONTH);
        int month = dateTimeFromInstant.get(ChronoField.MONTH_OF_YEAR) ; // one based
        int year = dateTimeFromInstant.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year;
        assertEquals(mmddyyyy, df.mapFrom(source,incomingFormat));
    }

    /*
     * Class under test for String format(Date, "")
     */
    @Test public void testDefaultMapInstant()
    {
        String source = df.format(Instant.now());

        TemporalAccessor dateTimeFromInstant = df.getDefaultFormat().parse(source);

        int day = dateTimeFromInstant.get(ChronoField.DAY_OF_MONTH);
        int month = dateTimeFromInstant.get(ChronoField.MONTH_OF_YEAR) ; // one based
        int year = dateTimeFromInstant.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String yyyymmdd = year + "-" + monthString + "-" + dayString;

        // caution we are mapping from the DateTimeFormatterTool defaultFormat7-pattern without time!
        // ISO_DATE_TIME will throw an error:
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: HourOfDay
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ISO_DATE.withZone(ZoneId.systemDefault());
        assertEquals(yyyymmdd, df.mapTo(source,outgoingFormat));

        outgoingFormat = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
        assertEquals(yyyymmdd, df.mapTo(source,outgoingFormat));

        // ISO_OFFSET_DATE :  Unsupported field: OffsetSeconds
        // ISO_INSTANT; Unsupported field: InstantSeconds
        yyyymmdd = year +  monthString + dayString;
        outgoingFormat = DateTimeFormatter.BASIC_ISO_DATE.withZone(ZoneId.systemDefault());
        assertEquals(yyyymmdd, df.mapTo(source,outgoingFormat));
    }
    
//    /*
//     * Class under test for String format(Date, "")
//     */
//    @Test public void testDefaultMapInstantString()
//    {
//        Instant today =  Instant.now();
//        String todayFormatted = df.format(today);
//        assertEquals("",
//                df.map(todayFormatted), "Empty pattern should produce empty String");
//    }

    /*
     * Class under test for String format(null, String)
     */
    @Test public void testMapDateStringNullString()
    {
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ISO_INSTANT;
        assertEquals("",
                df.mapFrom(null, outgoingFormat), "null argument should produce an empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */
    @Test public void testMapDateStringEmptyString()
    {
        Instant today =  Instant.now();
        String todayFormatted = df.format(today);
        assertEquals("",
                df.mapFrom(todayFormatted, null), "Empty pattern should map to empty String");
    }


    /*
     * Class under test for String format(null, String)
     */
    @Test public void testFormatDateStringNullString()
    {
        assertEquals("",
               df.format(null, "MM/dd/yyyy"), "null argument should produce an empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */
    @Test public void testFormatDateStringEmptyString()
    {
        Instant today =  Instant.now();
        assertEquals("",
                df.format(today, ""), "Empty pattern should produce empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */
    @Test public void testFormatDateStringNullFormat()
    {
        Instant today =  Instant.now();
        assertEquals("",
               df.format(today, null),"null pattern should produce empty String");
    }

}
