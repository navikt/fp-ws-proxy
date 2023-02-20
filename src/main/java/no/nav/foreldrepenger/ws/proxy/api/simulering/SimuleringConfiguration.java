package no.nav.foreldrepenger.ws.proxy.api.simulering;

import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class SimuleringConfiguration extends WsClient<SimulerFpService> {

    public SimuleringConfiguration(STSClient stsClient, Environment env) {
        super(stsClient, env);
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
