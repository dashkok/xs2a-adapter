package de.adorsys.xs2a.adapter.dkb;

import de.adorsys.xs2a.adapter.api.model.Aspsp;
import de.adorsys.xs2a.adapter.impl.security.AccessTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DkbPaymentInitiationServiceTest {
    private static final Aspsp ASPSP = buildAspspWithUrl();

    private final AccessTokenService accessService = Mockito.mock(AccessTokenService.class);
    private DkbPaymentInitiationService service = new DkbPaymentInitiationService(ASPSP, accessService, null, null);

    @Test
    void addBearerHeader() {
        when(accessService.retrieveToken()).thenReturn("token");

        Map<String, String> headers = service.addBearerHeader(new HashMap<>());

        verify(accessService, times(1)).retrieveToken();

        assertThat(headers)
            .hasSize(1)
            .containsKey("Authorization");
        assertThat(headers.get("Authorization")).isNotNull();
    }

    private static Aspsp buildAspspWithUrl() {
        Aspsp aspsp = new Aspsp();
        aspsp.setUrl("url");
        return aspsp;
    }
}
