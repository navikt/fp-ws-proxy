package no.nav.foreldrepenger.ws.proxy.api.gosys;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.ws.proxy.http.rs.AbstractConfig;

@ConfigurationProperties(prefix = "fpsak")
public class FpsakConfig extends AbstractConfig {
    private static final String DEFAULT_BASE_URI = "http://fpsak/fpsak";
    private static final String DEFAULT_PING_PATH = "/";

    @ConstructorBinding
    public FpsakConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                       @DefaultValue(DEFAULT_PING_PATH) String pingPath,
                       @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
    }
}
