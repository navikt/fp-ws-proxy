package no.nav.foreldrepenger.ws.proxy.sts;

import static no.nav.foreldrepenger.ws.proxy.http.WebClientConfiguration.STS;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class STSConnection {

    private static final Logger LOG = LoggerFactory.getLogger(STSConnection.class);
    private final STSConfig cfg;
    private final WebClient webClient;

    public STSConnection(@Qualifier(STS) WebClient webClient, STSConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
    }

    SystemToken refresh() {
        LOG.trace("Refresh av system token");
        var token = webClient
                .post()
                .uri(cfg::getStsURI)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(cfg.stsBody())
                .exchange()
                .block()
                .bodyToMono(SystemToken.class)
                .block();
        LOG.trace("Refresh av system token OK ({})", token.getExpiration());
        return token;
    }

    public Duration getSlack() {
        return cfg.getSlack();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[cfg=" + cfg + "]";
    }

}
