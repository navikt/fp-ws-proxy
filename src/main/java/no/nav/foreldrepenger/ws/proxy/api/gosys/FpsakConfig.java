package no.nav.foreldrepenger.ws.proxy.api.gosys;

import java.net.URI;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.http.rs.AbstractConfig;

@Component
public class FpsakConfig extends AbstractConfig {

    public FpsakConfig(URI baseUri, String pingPath, boolean enabled) {
        super(baseUri, pingPath, enabled);
    }
}
