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

package de.adorsys.xs2a.adapter.dkb;

import de.adorsys.xs2a.adapter.api.RequestHeaders;
import de.adorsys.xs2a.adapter.api.RequestParams;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.http.HttpClient;
import de.adorsys.xs2a.adapter.api.link.LinksRewriter;
import de.adorsys.xs2a.adapter.api.model.*;
import de.adorsys.xs2a.adapter.dkb.mapper.ConsentCreationResponseDkbMapper;
import de.adorsys.xs2a.adapter.dkb.mapper.StartScaProcessResponseDkbMapper;
import de.adorsys.xs2a.adapter.dkb.model.DkbConsentCreationResponse;
import de.adorsys.xs2a.adapter.dkb.model.DkbStartScaProcessResponse;
import de.adorsys.xs2a.adapter.impl.BaseAccountInformationService;
import de.adorsys.xs2a.adapter.impl.security.AccessTokenService;
import org.mapstruct.factory.Mappers;

import java.util.Map;

public class DkbAccountInformationService extends BaseAccountInformationService {
    private final StartScaProcessResponseDkbMapper startScaProcessResponseMapper = Mappers.getMapper(StartScaProcessResponseDkbMapper.class);
    private final ConsentCreationResponseDkbMapper creationResponseMapper = Mappers.getMapper(ConsentCreationResponseDkbMapper.class);
    private AccessTokenService accessService;

    public DkbAccountInformationService(Aspsp aspsp,
                                        AccessTokenService accessService,
                                        HttpClient httpClient,
                                        LinksRewriter linksRewriter) {
        super(aspsp, httpClient, linksRewriter);
        this.accessService = accessService;
    }

    @Override
    public Response<ConsentsResponse201> createConsent(RequestHeaders requestHeaders,
                                                       RequestParams requestParams,
                                                       Consents body) {
        return createConsent(requestHeaders,
            requestParams,
            body,
            DkbConsentCreationResponse.class,
            creationResponseMapper::toConsentCreationResponse);
    }

    @Override
    public Response<StartScaprocessResponse> startConsentAuthorisation(String consentId,
                                                                       RequestHeaders requestHeaders,
                                                                       RequestParams requestParams) {
        return startConsentAuthorisation(consentId,
            requestHeaders,
            requestParams,
            DkbStartScaProcessResponse.class,
            startScaProcessResponseMapper::toStartScaProcessResponse);
    }

    @Override
    public Response<StartScaprocessResponse> startConsentAuthorisation(String consentId,
                                                                       RequestHeaders requestHeaders,
                                                                       RequestParams requestParams,
                                                                       UpdatePsuAuthentication updatePsuAuthentication) {
        return startConsentAuthorisation(consentId,
            requestHeaders,
            requestParams,
            updatePsuAuthentication,
            DkbStartScaProcessResponse.class,
            startScaProcessResponseMapper::toStartScaProcessResponse);
    }

    @Override
    protected Map<String, String> populatePostHeaders(Map<String, String> map) {
        return addBearerHeader(map);
    }

    @Override
    protected Map<String, String> populatePutHeaders(Map<String, String> headers) {
        return addBearerHeader(headers);
    }

    @Override
    protected Map<String, String> populateGetHeaders(Map<String, String> headers) {
        return addBearerHeader(headers);
    }

    Map<String, String> addBearerHeader(Map<String, String> map) {
        map.put("Authorization", "Bearer " + accessService.retrieveToken());
        return map;
    }
}
