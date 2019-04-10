/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.xs2a.gateway.service.impl;

import de.adorsys.xs2a.gateway.service.Headers;
import de.adorsys.xs2a.gateway.service.consent.ConsentCreationResponse;
import de.adorsys.xs2a.gateway.service.consent.ConsentService;
import de.adorsys.xs2a.gateway.service.consent.Consents;

import java.util.Map;

public class DeutscheBankConsentService extends AbstractDeutscheBankService implements ConsentService {
    private static final String ESTABLISH_CONSENT_URI = BASE_DB_URI + "consents";

    @Override
    public ConsentCreationResponse createConsent(Consents body, Headers headers) {

        Map<String, String> headersMap = headers.toMap();
        addDBSpecificPostHeaders(headersMap);

        String bodyString = writeValueAsString(objectMapper.convertValue(body, Consents.class));

        return httpClient.post(ESTABLISH_CONSENT_URI, bodyString, headersMap,
                               postResponseHandler(ConsentCreationResponse.class));
    }
}
