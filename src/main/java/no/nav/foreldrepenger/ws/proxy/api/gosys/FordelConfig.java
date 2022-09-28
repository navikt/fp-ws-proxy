package no.nav.foreldrepenger.ws.proxy.api.gosys;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import no.nav.foreldrepenger.ws.proxy.http.rs.AbstractConfig;

@ConfigurationProperties(prefix = "fpfordel")
public class FordelConfig extends AbstractConfig {
    private static final String DEFAULT_BASE_URI = "http://fpfordel/fpfordel";
    private static final String DEFAULT_PING_PATH = "/";

    @ConstructorBinding
    public FordelConfig(@DefaultValue(DEFAULT_BASE_URI) URI baseUri,
                       @DefaultValue(DEFAULT_PING_PATH) String pingPath,
                       @DefaultValue("true") boolean enabled) {
        super(baseUri, pingPath, enabled);
    }
}
