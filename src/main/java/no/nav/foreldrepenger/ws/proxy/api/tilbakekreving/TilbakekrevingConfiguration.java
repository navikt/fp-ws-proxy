package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving;

import java.util.Objects;

import javax.xml.namespace.QName;

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

    private static final String WSDL = "wsdl/no/nav/tilbakekreving/tilbakekreving-v1-tjenestespesifikasjon.wsdl";
    private static final String NAMESPACE = "http://okonomi.nav.no/tilbakekrevingService/";
    private static final QName SERVICE = new QName(NAMESPACE, "TilbakekrevingService");
    private static final QName PORT = new QName(NAMESPACE, "TilbakekrevingServicePort");

    public TilbakekrevingConfiguration(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        super(endpointStsClientConfig, env);
    }

    @Bean
    public TilbakekrevingPortType tilbakekrevingKlient(@Value("${tilbakekreving.v1.url}") String serviceUrl) {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setWsdlURL(WSDL);
        jaxWsProxyFactoryBean.setServiceName(SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(PORT);
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        jaxWsProxyFactoryBean.setServiceClass(TilbakekrevingPortType.class);
        var port = (TilbakekrevingPortType) jaxWsProxyFactoryBean.create();
        return configureClientForSystemUser(port);
    }
}
