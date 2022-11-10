package no.nav.foreldrepenger.ws.proxy.api.simulering;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import no.nav.foreldrepenger.ws.proxy.http.ws.EndpointSTSClientConfig;
import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;

@Configuration
public class SimuleringConfiguration extends WsClient<SimulerFpService> {

    private static final String WSDL = "wsdl/no/nav/system/os/eksponering/simulerfpservicewsbinding.wsdl";

    public SimuleringConfiguration(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        super(endpointStsClientConfig, env);
    }

    @Bean
    public SimulerFpService simuleringKlient(@Value("${oppdrag.service.url}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, WSDL, SimulerFpService.class);
    }
}
