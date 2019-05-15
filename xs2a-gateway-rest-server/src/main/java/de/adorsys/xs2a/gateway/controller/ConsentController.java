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

package de.adorsys.xs2a.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.adorsys.xs2a.gateway.api.AccountApi;
import de.adorsys.xs2a.gateway.api.ConsentApi;
import de.adorsys.xs2a.gateway.mapper.*;
import de.adorsys.xs2a.gateway.model.ais.*;
import de.adorsys.xs2a.gateway.model.shared.ScaStatusResponseTO;
import de.adorsys.xs2a.gateway.model.shared.StartScaprocessResponseTO;
import de.adorsys.xs2a.gateway.service.*;
import de.adorsys.xs2a.gateway.service.account.AccountListHolder;
import de.adorsys.xs2a.gateway.service.account.TransactionsReport;
import de.adorsys.xs2a.gateway.service.ais.*;
import de.adorsys.xs2a.gateway.service.model.ScaStatusResponse;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
public class ConsentController extends AbstractController implements ConsentApi, AccountApi {

    private final AccountInformationService consentService;

    private final HeadersMapper headersMapper;

    private final ConsentMapper consentMapper = Mappers.getMapper(ConsentMapper.class);

    private final ConsentCreationResponseMapper creationResponseMapper = Mappers.getMapper(ConsentCreationResponseMapper.class);

    private final ConsentInformationMapper consentInformationMapper = Mappers.getMapper(ConsentInformationMapper.class);

    private final ConsentStatusResponseMapper consentStatusResponseMapper = Mappers.getMapper(ConsentStatusResponseMapper.class);

    private final AccountListHolderMapper accountListHolderMapper = Mappers.getMapper(AccountListHolderMapper.class);

    private final ScaStatusResponseMapper scaStatusResponseMapper = Mappers.getMapper(ScaStatusResponseMapper.class);

    public ConsentController(AccountInformationService consentService, ObjectMapper objectMapper, HeadersMapper headersMapper) {
        super(objectMapper);
        this.consentService = consentService;
        this.headersMapper = headersMapper;
    }

    @Override
    public ResponseEntity<ConsentsResponse201> createConsent(String bankCode, UUID xRequestId, ConsentsTO body, String digest, String signature, byte[] tppSignatureCertificate, String psuId, String psuIdType, String psuCorporateId, String psuCorporateIdType, boolean tppRedirectPreferred, String tppRedirectUri, String tppNokRedirectUri, boolean tppExplicitAuthorisationPreferred, String psuIpAddress, String psuIpPort, String psuAccept, String psuAcceptCharset, String psuAcceptEncoding, String psuAcceptLanguage, String psuUserAgent, String psuHttpMethod, UUID psuDeviceId, String psuGeoLocation) {
        RequestHeaders requestHeaders = buildCreateConsentHeaders(bankCode, xRequestId, digest, signature, tppSignatureCertificate, psuId, psuIdType, psuCorporateId, psuCorporateIdType, tppRedirectPreferred, tppRedirectUri, tppNokRedirectUri, tppExplicitAuthorisationPreferred, psuIpAddress, psuIpPort, psuAccept, psuAcceptCharset, psuAcceptEncoding, psuAcceptLanguage, psuUserAgent, psuHttpMethod, psuDeviceId, psuGeoLocation);
        Consents consents = consentMapper.toConsents(body);

        GeneralResponse<ConsentCreationResponse> response = consentService.createConsent(consents, requestHeaders);

        return ResponseEntity
                       .status(HttpStatus.CREATED)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(creationResponseMapper.toConsentResponse201(response.getResponseBody()));
    }

    @Override
    public ResponseEntity<ConsentInformationResponse200Json> getConsentInformation(String bankCode, String consentId, UUID xRequestID,
                                                                                   String digest, String signature, byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort,
                                                                                   String psUAccept, String psUAcceptCharset, String psUAcceptEncoding, String psUAcceptLanguage,
                                                                                   String psUUserAgent, String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {
        RequestHeaders requestHeaders = buildHeaders(bankCode, xRequestID, digest, signature, tpPSignatureCertificate, psUIPAddress, psUIPPort,
                psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod,
                psUDeviceID, psUGeoLocation);


        GeneralResponse<ConsentInformation> response = consentService.getConsentInformation(consentId, requestHeaders);

        return ResponseEntity
                       .status(HttpStatus.OK)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(consentInformationMapper.toConsentInformationResponse200Json(response.getResponseBody()));
    }

    @Override
    public ResponseEntity<ConsentStatusResponse200> getConsentStatus(String bankCode, String consentId, UUID xRequestID, String digest,
                                                                     String signature, byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort, String psUAccept,
                                                                     String psUAcceptCharset, String psUAcceptEncoding, String psUAcceptLanguage, String psUUserAgent,
                                                                     String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {
        RequestHeaders requestHeaders = buildHeaders(bankCode, xRequestID, digest, signature, tpPSignatureCertificate, psUIPAddress, psUIPPort,
                                       psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent, psUHttpMethod,
                                       psUDeviceID, psUGeoLocation);

        GeneralResponse<ConsentStatusResponse> response = consentService.getConsentStatus(consentId, requestHeaders);

        return ResponseEntity
                       .status(HttpStatus.OK)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(consentStatusResponseMapper.toConsentStatusResponse200(response.getResponseBody()));
    }

    private RequestHeaders buildCreateConsentHeaders(String bankCode, UUID xRequestId, String digest, String signature, byte[] tppSignatureCertificate, String psuId, String psuIdType, String psuCorporateId, String psuCorporateIdType, boolean tppRedirectPreferred, String tppRedirectUri, String tppNokRedirectUri, boolean tppExplicitAuthorisationPreferred, String psuIpAddress, String psuIpPort, String psuAccept, String psuAcceptCharset, String psuAcceptEncoding, String psuAcceptLanguage, String psuUserAgent, String psuHttpMethod, UUID psuDeviceId, String psuGeoLocation) {
        return RequestHeaders.builder()
                       .bankCode(bankCode)
                       .xRequestId(xRequestId)
                       .digest(digest)
                       .signature(signature)
                       .tppSignatureCertificate(tppSignatureCertificate)
                       .psuId(psuId)
                       .psuIdType(psuIdType)
                       .psuCorporateId(psuCorporateId)
                       .psuCorporateIdType(psuCorporateIdType)
                       .tppRedirectPreferred(tppRedirectPreferred)
                       .tppRedirectUri(tppRedirectUri)
                       .tppNokRedirectUri(tppNokRedirectUri)
                       .tppExplicitAuthorisationPreferred(tppExplicitAuthorisationPreferred)
                       .psuIpAddress(psuIpAddress)
                       .psuIpPort(psuIpPort)
                       .psuAccept(psuAccept)
                       .psuAcceptCharset(psuAcceptCharset)
                       .psuAcceptEncoding(psuAcceptEncoding)
                       .psuAcceptLanguage(psuAcceptLanguage)
                       .psuUserAgent(psuUserAgent)
                       .psuHttpMethod(psuHttpMethod)
                       .psuDeviceId(psuDeviceId)
                       .psuGeoLocation(psuGeoLocation)
                       .build();
    }

    @Override
    public ResponseEntity<StartScaprocessResponseTO> startConsentAuthorisation(
            String consentId,
            Map<String, String> headers,
            ObjectNode body) {
        RequestHeaders requestHeaders = RequestHeaders.fromMap(headers);
        GeneralResponse<?> response = handleAuthorisationBody(body,
                (UpdatePsuAuthenticationHandler) updatePsuAuthentication -> consentService.startConsentAuthorisation(consentId, requestHeaders, updatePsuAuthentication));


        return ResponseEntity
                       .status(HttpStatus.CREATED)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(startScaProcessResponseMapper.toStartScaprocessResponseTO((StartScaProcessResponse) response.getResponseBody()));
    }

    @Override
    public ResponseEntity<Object> updateConsentsPsuData(
            String consentId,
            String authorisationId,
            Map<String, String> headers,
            ObjectNode body) {
//        oneOf: #Different Authorisation Bodies
//                - {}
//                - $ref: "#/components/schemas/updatePsuAuthentication"
//                - $ref: "#/components/schemas/selectPsuAuthenticationMethod"
//                - $ref: "#/components/schemas/transactionAuthorisation"
//        oneOf: #Different Authorisation Bodies
//              - $ref: "#/components/schemas/updatePsuIdenticationResponse" #Update PSU Identification
//              - $ref: "#/components/schemas/updatePsuAuthenticationResponse" #Update PSU Authentication
//              - $ref: "#/components/schemas/selectPsuAuthenticationMethodResponse" #Select Authentication Method
//              - $ref: "#/components/schemas/scaStatusResponse" #Transaction Authorisation
        RequestHeaders requestHeaders = RequestHeaders.fromMap(headers);
        GeneralResponse<?> response = handleAuthorisationBody(body,
                (UpdatePsuAuthenticationHandler) updatePsuAuthentication -> consentService.updateConsentsPsuData(consentId, authorisationId, requestHeaders, updatePsuAuthentication),
                (SelectPsuAuthenticationMethodHandler) selectPsuAuthenticationMethod -> consentService.updateConsentsPsuData(consentId, authorisationId, requestHeaders, selectPsuAuthenticationMethod),
                (TransactionAuthorisationHandler) transactionAuthorisation -> consentService.updateConsentsPsuData(consentId, authorisationId, requestHeaders, transactionAuthorisation));

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                .body(response.getResponseBody());
    }

    @Override
    public ResponseEntity<AccountListTO> getAccountList(String bankCode, UUID xRequestID, String consentID, Boolean withBalance, String digest, String signature, byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort, String psUAccept, String psUAcceptCharset, String psUAcceptEncoding, String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {
        RequestHeaders requestHeaders = buildAccountGetHeaders(bankCode, xRequestID, consentID, digest, signature, tpPSignatureCertificate,
                psUIPAddress, psUIPPort, psUAccept, psUAcceptCharset, psUAcceptEncoding, psUAcceptLanguage, psUUserAgent,
                psUHttpMethod, psUDeviceID, psUGeoLocation);

        RequestParams requestParams = RequestParams.builder()
                                              .withBalance(withBalance)
                                              .build();

        GeneralResponse<AccountListHolder> response = consentService.getAccountList(requestHeaders, requestParams);

        return ResponseEntity.status(HttpStatus.OK)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(accountListHolderMapper.toAccountListTO(response.getResponseBody()));
    }

    @Override
    public ResponseEntity<?> getTransactionList(
            String accountId,
            String bookingStatus,
            LocalDate dateFrom,
            LocalDate dateTo,
            String entryReferenceFrom,
            Boolean deltaList,
            Boolean withBalance,
            Map<String, String> headers) {

        RequestHeaders requestHeaders = RequestHeaders.fromMap(headers);

        RequestParams requestParams = RequestParams.builder()
                                              .bookingStatus(bookingStatus)
                                              .dateFrom(dateFrom)
                                              .dateTo(dateTo)
                                              .entryReferenceFrom(entryReferenceFrom)
                                              .deltaList(deltaList)
                                              .withBalance(withBalance)
                                              .build();

        if (requestHeaders.isAcceptJson()) {
            GeneralResponse<TransactionsReport> transactionList = consentService.getTransactionList(accountId, requestHeaders, requestParams);
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headersMapper.toHttpHeaders(transactionList.getResponseHeaders()))
                    .body(transactionList.getResponseBody());
        }

        GeneralResponse<String> response = consentService.getTransactionListAsString(accountId, requestHeaders, requestParams);

        return ResponseEntity
                       .status(HttpStatus.OK)
                       .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                       .body(response.getResponseBody());
    }

    private RequestHeaders buildAccountGetHeaders(String bankCode, UUID xRequestID, String consentID, String digest, String signature, byte[] tpPSignatureCertificate, String psUIPAddress, String psUIPPort, String psUAccept, String psUAcceptCharset, String psUAcceptEncoding, String psUAcceptLanguage, String psUUserAgent, String psUHttpMethod, UUID psUDeviceID, String psUGeoLocation) {
        return RequestHeaders.builder()
                       .bankCode(bankCode)
                       .xRequestId(xRequestID)
                       .consentId(consentID)
                       .digest(digest)
                       .signature(signature)
                       .tppSignatureCertificate(tpPSignatureCertificate)
                       .psuIpAddress(psUIPAddress)
                       .psuIpPort(psUIPPort)
                       .psuAccept(psUAccept)
                       .psuAcceptCharset(psUAcceptCharset)
                       .psuAcceptEncoding(psUAcceptEncoding)
                       .psuAcceptLanguage(psUAcceptLanguage)
                       .psuUserAgent(psUUserAgent)
                       .psuHttpMethod(psUHttpMethod)
                       .psuDeviceId(psUDeviceID)
                       .psuGeoLocation(psUGeoLocation)
                       .build();
    }

    @Override
    public ResponseEntity<ScaStatusResponseTO> getConsentScaStatus(String consentId, String authorisationId, Map<String, String> headers) {
        RequestHeaders requestHeaders = RequestHeaders.fromMap(headers);

        GeneralResponse<ScaStatusResponse> response = consentService.getConsentScaStatus(consentId, authorisationId, requestHeaders);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headersMapper.toHttpHeaders(response.getResponseHeaders()))
                .body(scaStatusResponseMapper.toScaStatusResponseTO(response.getResponseBody()));
    }
}
