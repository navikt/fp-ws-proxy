package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.okonomi.tilbakekrevingservice.TilbakekrevingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class TilbakekrevingConfiguration extends WsClient<TilbakekrevingPortType> {

    public TilbakekrevingConfiguration(STSClient stsClient, Environment env) {
        super(stsClient, env);
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
