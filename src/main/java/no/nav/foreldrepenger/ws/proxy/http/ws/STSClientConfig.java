package no.nav.foreldrepenger.ws.proxy.http.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "securitytokenservice")
public record STSClientConfig(URI url, String username, String password) {

}
