package de.adorsys.xs2a.adapter.api.exception;

import de.adorsys.xs2a.adapter.api.ResponseHeaders;
import de.adorsys.xs2a.adapter.api.model.ErrorResponse;

import java.util.Optional;

// Following error schemas don't have the body:
// Error406_NG_PIS
// Error408_NG_PIS
// Error415_NG_PIS
// Error429_NG_PIS
// Error500_NG_PIS
// Error503_NG_PIS
public class ErrorResponseException extends RuntimeException {
    private final int statusCode;
    private final transient ResponseHeaders responseHeaders;
    private final transient ErrorResponse errorResponse;

    public ErrorResponseException(int statusCode,
                                  ResponseHeaders responseHeaders,
                                  ErrorResponse errorResponse,
                                  String response) {
        super(response);
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.errorResponse = errorResponse;
    }

    public ErrorResponseException(int statusCode, ResponseHeaders responseHeaders, ErrorResponse errorResponse) {
        this(statusCode, responseHeaders, errorResponse, null);
    }

    public ErrorResponseException(int statusCode, ResponseHeaders responseHeaders) {
        this(statusCode, responseHeaders, null);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<ErrorResponse> getErrorResponse() {
        return Optional.ofNullable(errorResponse);
    }

    public ResponseHeaders getResponseHeaders() {
        return responseHeaders;
    }
}
