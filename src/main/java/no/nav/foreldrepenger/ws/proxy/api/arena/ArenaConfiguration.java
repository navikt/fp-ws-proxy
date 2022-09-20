package no.nav.foreldrepenger.ws.proxy.api.arena;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.ws.proxy.ws.EndpointSTSClientConfig;
import no.nav.foreldrepenger.ws.proxy.ws.WsClient;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;

@Configuration
public class ArenaConfiguration extends WsClient<MeldekortUtbetalingsgrunnlagV1> {

    public ArenaConfiguration(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        super(endpointStsClientConfig, env);
    }

    @Bean
    public MeldekortUtbetalingsgrunnlagV1 client(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) { // TODO: Riktig url
        return createPortForSystemUser(serviceUrl, MeldekortUtbetalingsgrunnlagV1.class);
    }

    @Bean
    public ArenaKlientWs bankkontoKlient(MeldekortUtbetalingsgrunnlagV1 client) {
        return new ArenaKlientWs(client);
    }
}
