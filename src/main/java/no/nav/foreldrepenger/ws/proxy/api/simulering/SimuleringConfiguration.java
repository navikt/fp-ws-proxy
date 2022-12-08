package no.nav.foreldrepenger.ws.proxy.api.simulering;

import java.util.Objects;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.ws.proxy.http.ws.EndpointSTSClientConfig;
import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;

@Configuration
public class SimuleringConfiguration extends WsClient<SimulerFpService> {

    public SimuleringConfiguration(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        super(endpointStsClientConfig, env);
    }

    @Bean
    public SimulerFpService simuleringKlient(@Value("${oppdrag.service.url}") String serviceUrl) {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        jaxWsProxyFactoryBean.setServiceClass(SimulerFpService.class);
        var port = (SimulerFpService) jaxWsProxyFactoryBean.create();
        return configureClientForSystemUser(port);
    }
}
