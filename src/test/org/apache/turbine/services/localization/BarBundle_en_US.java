package org.apache.turbine.services.localization;

import java.util.ListResourceBundle;

/**
 * An english resource bundle for use in testing.
 */
public class BarBundle_en_US extends ListResourceBundle
{
    private static final Object[][] CONTENTS =
    {
        { "key1", "value1" },
        { "key2", "value2" },
        { "key3", "value3" },
        { "key4", "value4" }
    };

    protected Object[][] getContents()
    {
        return CONTENTS;
    }
}
