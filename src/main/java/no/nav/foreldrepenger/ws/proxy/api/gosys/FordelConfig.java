package no.nav.foreldrepenger.ws.proxy.api.gosys;

import java.net.URI;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.http.rs.AbstractConfig;

@Component
public class FordelConfig extends AbstractConfig {

    public FordelConfig(URI baseUri, String pingPath, boolean enabled) {
        super(baseUri, pingPath, enabled);
    }
}
