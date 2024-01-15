package org.apache.turbine.services.localization;

import java.io.Serializable;
import java.util.Locale;

import org.apache.fulcrum.localization.LocalizationService;
import org.apache.turbine.util.RunData;

public interface RundataLocalizationInterface extends LocalizationService, Serializable
{
    /**
     * Get the locale from the session first, then fallback to normal request headers.
     * 
     * @param data the {@link RunData}, which allows a guess for the locale.
     * @return Current locale based on state.
     */
    Locale getLocale(RunData data);
    
}
