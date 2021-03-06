package de.adorsys.xs2a.adapter.sparkasse;

import de.adorsys.xs2a.adapter.api.Oauth2Service;
import de.adorsys.xs2a.adapter.api.PkceOauth2Extension;
import de.adorsys.xs2a.adapter.api.Pkcs12KeyStore;
import de.adorsys.xs2a.adapter.api.http.HttpClient;
import de.adorsys.xs2a.adapter.api.model.Aspsp;
import de.adorsys.xs2a.adapter.api.model.TokenResponse;
import de.adorsys.xs2a.adapter.impl.*;
import de.adorsys.xs2a.adapter.impl.http.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static de.adorsys.xs2a.adapter.api.validation.Validation.requireValid;

public class SparkasseOauth2Service extends Oauth2ServiceDecorator implements PkceOauth2Extension {

    private static final String AIS_SCOPE_PREFIX = "AIS: ";
    private static final String PIS_SCOPE_PREFIX = "PIS: ";

    private SparkasseOauth2Service(Oauth2Service oauth2Service) {
        super(oauth2Service);
    }

    public static SparkasseOauth2Service create(Aspsp aspsp, HttpClient httpClient, Pkcs12KeyStore keyStore) {
        BaseOauth2Service baseOauth2Service = new BaseOauth2Service(aspsp, httpClient);
        CertificateSubjectClientIdOauth2Service clientIdOauth2Service =
            new CertificateSubjectClientIdOauth2Service(baseOauth2Service, keyStore);
        PkceOauth2Service pkceOauth2Service = new PkceOauth2Service(clientIdOauth2Service);
        ScopeWithResourceIdOauth2Service scopeOauth2Service =
            new ScopeWithResourceIdOauth2Service(pkceOauth2Service, AIS_SCOPE_PREFIX, PIS_SCOPE_PREFIX);
        return new SparkasseOauth2Service(scopeOauth2Service);
    }

    @Override
    public URI getAuthorizationRequestUri(Map<String, String> headers, Parameters parameters) throws IOException {
        requireValid(validateGetAuthorizationRequestUri(headers, parameters));
        return UriBuilder.fromUri(oauth2Service.getAuthorizationRequestUri(headers, parameters))
            .renameQueryParam(Parameters.RESPONSE_TYPE, "responseType")
            .renameQueryParam(Parameters.CLIENT_ID, "clientId")
            .build();
    }

    @Override
    public TokenResponse getToken(Map<String, String> headers, Parameters parameters) throws IOException {
        return oauth2Service.getToken(headers, parameters);
    }
}
