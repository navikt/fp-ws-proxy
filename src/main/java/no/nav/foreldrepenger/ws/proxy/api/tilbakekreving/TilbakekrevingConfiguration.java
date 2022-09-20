package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.ws.proxy.http.ws.EndpointSTSClientConfig;
import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingPortType;

@Configuration
public class TilbakekrevingConfiguration extends WsClient<TilbakekrevingPortType> {

    public TilbakekrevingConfiguration(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        super(endpointStsClientConfig, env);
    }

    @Bean
    public TilbakekrevingPortType client(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) { // TODO: Riktig url
        return createPortForSystemUser(serviceUrl, TilbakekrevingPortType.class);
    }

    @Bean
    public TilbakekrevingKlientWs bankkontoKlient(TilbakekrevingPortType klient) {
        return new TilbakekrevingKlientWs(klient);
    }
}
