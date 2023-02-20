package no.nav.foreldrepenger.ws.proxy.http.ws;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

@Component
public class STSClientBeanConfiguration {
    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";

    private final STSClientConfig cfg;

    public STSClientBeanConfiguration(STSClientConfig cfg) {
        this.cfg = cfg;
    }

    @Bean
    public STSClient configureSTSClient(Bus bus) {
        var sts = new STSClient(bus);
        sts.setEnableAppliesTo(false);
        sts.setAllowRenewing(false);
        sts.setLocation(cfg.url().toString());
        sts.setProperties(Map.of(
            USERNAME, cfg.username(),
            PASSWORD, cfg.password()));

        sts.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY); // used for the STS client to authenticate itself to the STS provider.
        var loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        var loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);
        sts.getInFaultInterceptors().add(loggingInInterceptor);
        sts.getOutFaultInterceptors().add(loggingOutInterceptor);
        return sts;
    }
}
