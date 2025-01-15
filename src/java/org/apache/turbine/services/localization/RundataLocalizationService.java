package org.apache.turbine.services.localization;

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

import java.util.Locale;

import org.apache.fulcrum.localization.DefaultLocalizationService;
import org.apache.fulcrum.localization.LocalizationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.RunData;

/**
 * 
 * Instead of reading first the accept-language header in a http request,
 * instead this method read the user.getTemp("locale")
 * from the RunData to obtain the language choice by the user
 * without the browser language rule.
 * If user.getPerm("language") is not set,
 *  the "Accept-Language" header is read.
 * 
 * Adapted from the Jetspeed-1 implementation of CustomLocalizationService.
 * 
 */
public class RundataLocalizationService extends DefaultLocalizationService implements RundataLocalizationInterface {

    private static final Logger log = LogManager.getLogger(RundataLocalizationService.class); 
    
    @Override
    public Locale getLocale(RunData data) {
        User user = data.getUser();
        log.debug( "retrieving lang from req header :{}",
                (user == null || user.getTemp("locale") == null )  );

        if (user == null)
        {
            return getLocale(data.getRequest().getHeader(LocalizationService.ACCEPT_LANGUAGE));
        }
        else
        {
            try
            {
                Locale locale = (Locale) data.getUser().getTemp("locale");
                if (locale == null)
                {
                    return getLocale(data.getRequest().getHeader(LocalizationService.ACCEPT_LANGUAGE));
                }
                else
                {
                    log.debug( "retrieved lang from temp(locale):{}", ()-> locale.getLanguage() );
                    return locale;
                }
            }
            catch (Exception use)
            {
                return getLocale(data.getRequest().getHeader(LocalizationService.ACCEPT_LANGUAGE));
            }
        }
    }

}
