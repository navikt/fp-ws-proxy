package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import java.util.Objects;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
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
    public TilbakekrevingPortType tilbakekrevingKlient(@Value("${tilbakekreving.v1.url}") String serviceUrl) {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        jaxWsProxyFactoryBean.setServiceClass(TilbakekrevingPortType.class);
        var port = (TilbakekrevingPortType) jaxWsProxyFactoryBean.create();
        return configureClientForSystemUser(port);
    }
}
