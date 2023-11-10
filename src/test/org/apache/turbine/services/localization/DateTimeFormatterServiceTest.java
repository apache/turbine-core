package org.apache.turbine.services.localization;

import static org.junit.Assert.assertTrue;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.util.DateTimeFormatterTool;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.context.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test class for DateTimeFormatter.
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DateTimeFormatterServiceTest extends BaseTestCase {

    @TurbineService
    private DateTimeFormatterService df;

    private TurbineConfig tc = null;

    @TurbineService
    private PullService pullService;

    DateTimeFormatterTool dateTimeFormatterTool;

    @TurbineService
    private VelocityService vs = null;

    @TurbineService
    RunDataService runDataService = null;

    @BeforeAll
    public void setup() throws Exception {
        // required to initialize defaults
        tc = new TurbineConfig(
                ".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();

        AnnotationProcessor.process(this);

        assertNotNull(pullService);
        assertNotNull(vs);
    }

    private RunData getRunData() throws Exception {
        ServletConfig config = mock(ServletConfig.class);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        RunData runData = runDataService.getRunData(request, response, config);
        assertEquals(DefaultParameterParser.class, runData.getParameters()
                .getClass(), "Verify we are using Fulcrum parameter parser");
        return runData;
    }

    @AfterAll
    public void tearDown() {
        vs.shutdown();
        tc.dispose();
    }

    /*
     * Class under test for String format(Date, String)
     */
    @Order(1)
    @Test
    void testTool() throws Exception {
        RunData rundata = getRunData();
        Context requestContext = vs.getContext(rundata);
        assertNotNull(requestContext);
        pullService.populateContext(requestContext, rundata);

        // taking from request context
        dateTimeFormatterTool = (DateTimeFormatterTool) requestContext.get("dateTimeFormatter");
        assertNotNull(dateTimeFormatterTool);
        
        String resultFormat = dateTimeFormatterTool.format(Instant.now());
        
        System.out.println("format Instant now in tool:"+resultFormat );
        assertTrue(resultFormat.length()>5);
        
        System.out.println("locale in tool:"+ dateTimeFormatterTool.getLocale());
        // tool.use.request.locale is by default false, tool will use service locale
        assertTrue(dateTimeFormatterTool.getLocale() == null);
    }
    
    // to test configuration 
    // datetime.zoneId
    // and locale.default.language l.d.country
    @Test
    void testDefault() throws Exception {
        assertNotNull(df);
        System.out.println("zoneid in service:"+ df.getZoneId().getId());
//        assertEquals("Europe/Berlin",df.getZoneId().getId());
        System.out.println("locale in service:"+ df.getLocale());
//        assertEquals("de_DE",df.getLocale().toString());
 
    }

    @Order(2)
    @TestFactory
    Stream<DynamicNode> testDateTimeFormatterInstances() {
        // Stream of DateTimeFormatterInterface to check
        Stream<DateTimeFormatterInterface> inputStream = Stream.of(
                df, 
                dateTimeFormatterTool);
        // Executes tests based on the current input value.
        return inputStream.map(dtf -> dynamicContainer(
                "Test " + dtf + " in factory container:",
                Stream.of(
                        dynamicTest("test formatDateString",() -> formatDateString(dtf) ),
                        dynamicTest("test formatZonedDateString",() -> formatZonedDateString(dtf) ),
                        dynamicTest("test defaultMapFromInstant",() -> defaultMapFromInstant(dtf) ),
                        dynamicTest("test defaultMapInstant",() -> defaultMapInstant(dtf) ),
                        dynamicTest("test mapDateStringNullString", () -> mapDateStringNullString(dtf)),
                        dynamicTest("test mapDateStringEmptyString",() -> mapDateStringEmptyString(dtf)),
                        dynamicTest("test formatDateStringNullFormat",() -> formatDateStringNullFormat(dtf)),
                        dynamicTest("test formatDateStringNullString",() -> formatDateStringNullString(dtf)),
                        dynamicTest("test formatDateStringEmptyString",() -> formatDateStringEmptyString(dtf)),
                        dynamicTest("test formatInstantString", () -> formatInstantString(dtf))
                        )));
        // Or return a stream of dynamic tests instead of Dynamic nodes,
        // but this requires Function<DateTimeFormatterInterface, String>
        // displayNameGenerator and
        // e.g. ThrowingConsumer<DateTimeFormatterInterface> testExecutor = dtf
        // return DynamicTest.stream(inputStream, displayNameGenerator, testExecutor);
    }

    void formatDateString(DateTimeFormatterInterface dateTime) {
        LocalDateTime ldt = LocalDateTime.now();
        int day = ldt.get(ChronoField.DAY_OF_MONTH);
        int month = ldt.get(ChronoField.MONTH_OF_YEAR); // one based
        int year = ldt.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String ddmmyyyy = dayString + "/" + monthString + "/" + year;

        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year;

        assertEquals(ddmmyyyy, dateTime.format(ldt, "dd/MM/yyyy"));
        assertEquals(mmddyyyy, dateTime.format(ldt, "MM/dd/yyyy"));
    }

    void formatZonedDateString(DateTimeFormatterInterface dateTime) {        
        ZonedDateTime zdt = ZonedDateTime.now(dateTime.getZoneId());
        int day = zdt.get(ChronoField.DAY_OF_MONTH);
        int month = zdt.get(ChronoField.MONTH_OF_YEAR); // one based
        int year = zdt.get(ChronoField.YEAR);
        zdt = zdt.truncatedTo(ChronoUnit.MINUTES);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String ddmmyyyy = dayString + "/" + monthString + "/" + year;
        Assertions.assertEquals(ddmmyyyy, df.format(zdt, "dd/MM/yyyy"));

        int hours = zdt.get(ChronoField.HOUR_OF_DAY);
        int mins = zdt.get(ChronoField.MINUTE_OF_HOUR);
        int secs = zdt.get(ChronoField.SECOND_OF_MINUTE);
        String hourString = (hours < 10 ? "0" : "") + hours;
        String minsString = (mins < 10 ? "0" : "") + mins;
        String secsString = (secs < 10 ? "0" : "") + secs;

        String zone = zdt.getZone().getId();
        /* String offset = */ zdt.getOffset().getId();
        // offset formatting not easy matchable, removed
        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year + " " + hourString + ":" + minsString + ":"
                + secsString + " " + zone;
        // zone + offset format, removed offset ZZZ
        assertEquals(mmddyyyy, dateTime.format(zdt, "MM/dd/yyyy HH:mm:ss VV"));
    }

    void defaultMapFromInstant(DateTimeFormatterInterface dateTime) {
        DateTimeFormatter incomingFormat = DateTimeFormatter.ISO_DATE_TIME
                .withZone(dateTime.getZoneId());
        // may throws an DateTimeParseException
        Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        String source = incomingFormat.format(now);

        TemporalAccessor dateTimeFromInstant = incomingFormat.parse(source);
        int day = dateTimeFromInstant.get(ChronoField.DAY_OF_MONTH);
        int month = dateTimeFromInstant.get(ChronoField.MONTH_OF_YEAR); // one based
        int year = dateTimeFromInstant.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year;
        assertEquals(mmddyyyy, dateTime.mapFrom(source, incomingFormat));
    }

    void defaultMapInstant(DateTimeFormatterInterface dateTime) {
        String source = dateTime.format(Instant.now());

        TemporalAccessor dateTimeFromInstant = dateTime.getDefaultFormat().parse(source);

        int day = dateTimeFromInstant.get(ChronoField.DAY_OF_MONTH);
        int month = dateTimeFromInstant.get(ChronoField.MONTH_OF_YEAR); // one based
        int year = dateTimeFromInstant.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String yyyymmdd = year + "-" + monthString + "-" + dayString;

        // caution we are mapping from the DateTimeFormatterTool defaultFormat-pattern
        // without time!
        // ISO_DATE_TIME will throw an error:
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field:
        // HourOfDay
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ISO_DATE.withZone(ZoneId.systemDefault());
        Assertions.assertEquals(yyyymmdd, dateTime.mapTo(source, outgoingFormat));

        outgoingFormat = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
        Assertions.assertEquals(yyyymmdd, dateTime.mapTo(source, outgoingFormat));

        // ISO_OFFSET_DATE : Unsupported field: OffsetSeconds
        // ISO_INSTANT; Unsupported field: InstantSeconds
        yyyymmdd = year + monthString + dayString;
        outgoingFormat = DateTimeFormatter.BASIC_ISO_DATE.withZone(ZoneId.systemDefault());
        assertEquals(yyyymmdd, dateTime.mapTo(source, outgoingFormat));
    }

    /*
     * Class under test for String format(null, String)
     */
    void mapDateStringNullString(DateTimeFormatterInterface dateTime) {
        DateTimeFormatter outgoingFormat = DateTimeFormatter.ISO_INSTANT;
        Assertions.assertEquals("",
                dateTime.mapFrom(null, outgoingFormat), "null argument should produce an empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */
    void mapDateStringEmptyString(DateTimeFormatterInterface dateTime) {
        Instant today = Instant.now();
        String todayFormatted = df.format(today);
        Assertions.assertEquals("",
                dateTime.mapFrom(todayFormatted, null), "Empty pattern should map to empty String");
    }

    /*
     * Class under test for String format(null, String)
     */
    void formatDateStringNullString(DateTimeFormatterInterface dateTime) {
        Assertions.assertEquals("",
                dateTime.format(null, "MM/dd/xyyyy"), "null argument should produce an empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */
    void formatDateStringEmptyString(DateTimeFormatterInterface dateTime) {
        Instant today = Instant.now();
        Assertions.assertEquals("",
                dateTime.format(today, ""), "Empty pattern should produce empty String");
    }

    /*
     * Class under test for String format(Date, "")
     */

    void formatDateStringNullFormat(DateTimeFormatterInterface dateTime) {
        Instant today = Instant.now();
        Assertions.assertEquals("",
                dateTime.format(today, null), "null pattern should produce empty String");
    }

    void formatInstantString(DateTimeFormatterInterface dateTime) {
       
        ZonedDateTime zonedToday = ZonedDateTime.now(dateTime.getZoneId());
        int day = zonedToday.get(ChronoField.DAY_OF_MONTH);
        int month = zonedToday.get(ChronoField.MONTH_OF_YEAR); // one based
        int year = zonedToday.get(ChronoField.YEAR);

        String dayString = (day < 10 ? "0" : "") + day;
        String monthString = (month < 10 ? "0" : "") + month;
        String ddmmyyyy = dayString + "/" + monthString + "/" + year;
        String mmddyyyy = "" + monthString + "/" + dayString + "/" + year;
        assertNotNull(ddmmyyyy);
        assertNotNull(mmddyyyy);
        
        Instant today = Instant.now();
        assertNotNull(dateTime.format(today, "dd/MM/yyyy"));
        assertNotNull(dateTime.format(today, "MM"));
        assertEquals(ddmmyyyy, dateTime.format(today, "dd/MM/yyyy"));
        assertEquals(mmddyyyy, dateTime.format(today, "MM/dd/yyyy"));
    }

}
