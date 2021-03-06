package de.adorsys.xs2a.adapter.ing;

import de.adorsys.xs2a.adapter.api.RequestHeaders;
import de.adorsys.xs2a.adapter.api.Response;
import de.adorsys.xs2a.adapter.api.http.HttpClient;
import de.adorsys.xs2a.adapter.api.http.Interceptor;
import de.adorsys.xs2a.adapter.api.model.CardAccountsTransactionsResponse200;
import de.adorsys.xs2a.adapter.impl.http.StringUri;
import de.adorsys.xs2a.adapter.ing.model.IngAccountsResponse;
import de.adorsys.xs2a.adapter.ing.model.IngBalancesResponse;
import de.adorsys.xs2a.adapter.ing.model.IngTransactionsResponse;

import java.time.LocalDate;
import java.util.*;

import static de.adorsys.xs2a.adapter.impl.http.ResponseHandlers.jsonResponseHandler;

public class IngAccountInformationApi {
    private static final String ACCOUNTS_ENDPOINT = "/v3/accounts";
    private static final String TRANSACTIONS_ENDPOINT = "/v2/accounts/{{accountId}}/transactions";
    private static final String BALANCES_ENDPOINT = "/v3/accounts/{{accountId}}/balances";
    private static final String CARD_ACCOUNT_TRANSACTIONS_ENDPOINT = "/v1/card-accounts/{{accountId}}/transactions";
    private static final String ACCOUNT_ID_PLACEHOLDER = "{{accountId}}";

    private final String baseUri;
    private final HttpClient httpClient;

    public IngAccountInformationApi(String baseUri, HttpClient httpClient) {
        this.baseUri = baseUri;
        this.httpClient = httpClient;
    }

    public Response<IngAccountsResponse> getAccounts(String requestId, Interceptor clientAuthentication) {
        return httpClient.get(baseUri + ACCOUNTS_ENDPOINT)
                   .header(RequestHeaders.X_REQUEST_ID, requestId)
                   .send(clientAuthentication, jsonResponseHandler(IngAccountsResponse.class));
    }

    public Response<IngTransactionsResponse> getTransactions(String resourceId,
                                                             LocalDate dateFrom,
                                                             LocalDate dateTo,
                                                             Currency currency,
                                                             Integer limit,
                                                             String requestId,
                                                             Interceptor clientAuthentication) {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("dateFrom", dateFrom);
        queryParams.put("dateTo", dateTo);
        queryParams.put("currency", currency);
        queryParams.put("limit", limit);
        String uri = StringUri.withQuery(
            baseUri + TRANSACTIONS_ENDPOINT.replace(ACCOUNT_ID_PLACEHOLDER, Objects.requireNonNull(resourceId)),
            queryParams
        );

        return httpClient.get(uri)
                   .header(RequestHeaders.X_REQUEST_ID, requestId)
                   .send(clientAuthentication, jsonResponseHandler(IngTransactionsResponse.class));
    }

    /**
     * @param balanceTypes (optional) A comma separated list of ISO20022 balance type(s)
     * @param currency     (optional/conditional) 3 Letter ISO Currency Code (ISO 4217)
     *                     for which transactions are requested. Required in case
     *                     transactions are requested for a multi-currency account.
     */
    public Response<IngBalancesResponse> getBalances(String resourceId,
                                                     List<String> balanceTypes,
                                                     Currency currency,
                                                     String requestId,
                                                     Interceptor clientAuthentication) {
        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("balanceTypes", balanceTypes == null ? null : String.join(",", balanceTypes));
        queryParams.put("currency", currency);
        String uri = StringUri.withQuery(
            baseUri + BALANCES_ENDPOINT.replace(ACCOUNT_ID_PLACEHOLDER, Objects.requireNonNull(resourceId)),
            queryParams
        );

        return httpClient.get(uri)
                   .header(RequestHeaders.X_REQUEST_ID, requestId)
                   .send(clientAuthentication, jsonResponseHandler(IngBalancesResponse.class));
    }

    public Response<CardAccountsTransactionsResponse200> getCardAccountTransactions(
        String accountId,
        LocalDate dateFrom,
        LocalDate dateTo,
        Integer limit,
        String requestId,
        Interceptor clientAuthentication) {

        Map<String, Object> queryParams = new LinkedHashMap<>();
        queryParams.put("dateFrom", dateFrom);
        queryParams.put("dateTo", dateTo);
        queryParams.put("limit", limit);
        String uri = StringUri.withQuery(
            baseUri + CARD_ACCOUNT_TRANSACTIONS_ENDPOINT.replace(ACCOUNT_ID_PLACEHOLDER, Objects.requireNonNull(accountId)),
            queryParams
        );

        return httpClient.get(uri)
                   .header(RequestHeaders.X_REQUEST_ID, requestId)
                   .send(clientAuthentication, jsonResponseHandler(CardAccountsTransactionsResponse200.class));
    }
}
