package org.apache.turbine.annotation;



import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.localization.DateTimeFormatterInterface;
import org.apache.turbine.services.localization.DateTimeFormatterService;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.pull.util.DateTimeFormatterTool;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.apache.velocity.context.Context;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for AnnotatedMethodsTest to test method fields annotation
 *
 */
public class AnnotatedMethodsTest {

    private static AssemblerBrokerService asb;
    private static TurbineConfig tc = null;
    private static PullService pullService;

    @BeforeAll
    public static void setup()
    {
        // required to initialize defaults
        tc = new TurbineConfig(
                        ".",
                        "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @AfterAll
    public static void tearDown()
    {
        tc.dispose();
    }

    @TurbineService
    public void setAssemblerBrokerService(AssemblerBrokerService df)
    {
        AnnotatedMethodsTest.asb = df;
    }

    @TurbineService
    public static void setPullService(PullService pullService) {
        AnnotatedMethodsTest.pullService = pullService;
    }

    /*
     * Class under test for String format(Date, String)
     */
    @Test
    void testTool() throws TurbineException
    {
        AnnotationProcessor.process(this, true);
        assertNotNull(pullService);
        assertNotNull(asb);
    }

    @Tag("performance") // ignore in surefire, activating seems to be still buggy ?
    @Test
    public void testProcessingPerformance() throws TurbineException
    {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++)
        {
            AnnotationProcessor.process(this, true);
        }

        System.out.println(System.currentTimeMillis() - startTime);
    }

}
