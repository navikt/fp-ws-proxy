package no.nav.foreldrepenger.ws.proxy.http;

import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID1;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CALL_ID2;
import static no.nav.foreldrepenger.common.util.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.consumerId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import no.nav.foreldrepenger.common.util.MDCUtil;
import no.nav.foreldrepenger.ws.proxy.api.gosys.FordelConfig;
import no.nav.foreldrepenger.ws.proxy.api.gosys.FpsakConfig;
import no.nav.foreldrepenger.ws.proxy.sts.STSConfig;
import no.nav.foreldrepenger.ws.proxy.sts.SystemTokenTjeneste;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfiguration {

    public static final String STS = "STS";
    public static final String FPSAK = "FPSAK";
    public static final String FPFORDEL = "FPFORDEL";

    @Bean
    public WebClientCustomizer fellesWebKlientKonfig(Environment env) {
        var provider = ConnectionProvider.builder("custom")
            .maxConnections(50)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofSeconds(60))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120))
            .build();
        return webClientBuilder -> webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider).wiretap(isDevOrLocal(env))))
            .filter(correlatingFilterFunction())
            .build();
    }

    @Bean
    @Qualifier(STS)
    public WebClient webClientSTS(Builder builder, STSConfig cfg) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .defaultHeaders(h -> h.setBasicAuth(cfg.getUsername(), cfg.getPassword()))
            .build();
    }

    @Bean
    @Qualifier(FPFORDEL)
    public WebClient webClientFpfordelSystem(Builder builder, FordelConfig cfg, SystemTokenTjeneste sts) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(systemUserExchangeFilterFunction(sts))
            .build();
    }

    @Bean
    @Qualifier(FPSAK)
    public WebClient webClientFpsakSystem(Builder builder, FpsakConfig cfg, SystemTokenTjeneste sts) {
        return builder
            .baseUrl(cfg.getBaseUri().toString())
            .filter(systemUserExchangeFilterFunction(sts))
            .build();
    }

    private ExchangeFilterFunction correlatingFilterFunction() {
        return (req, next) -> next.exchange(ClientRequest.from(req)
            .header(NAV_CONSUMER_ID, consumerId())
            .header(NAV_CALL_ID, MDCUtil.callId())
            .header(NAV_CALL_ID1, MDCUtil.callId())
            .header(NAV_CALL_ID2, MDCUtil.callId())
            .build());
    }

    private static ExchangeFilterFunction systemUserExchangeFilterFunction(SystemTokenTjeneste sts) {
        return (req, next) -> next.exchange(ClientRequest.from(req)
            .header(AUTHORIZATION, sts.bearerToken())
            .build());
    }
}
