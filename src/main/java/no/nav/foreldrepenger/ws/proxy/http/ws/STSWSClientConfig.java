package no.nav.foreldrepenger.ws.proxy.http.ws;

import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

import java.net.URI;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "securitytokenservice")
public class STSWSClientConfig {
    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";

    private final URI url;
    private final String username;
    private final String password;

    @ConstructorBinding
    public STSWSClientConfig(URI url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Bean
    public STSClient configureSTSClient(Bus bus) {
        var sts = new STSClient(bus);
        sts.setEnableAppliesTo(false);
        sts.setAllowRenewing(false);
        sts.setLocation(url.toString());
        sts.setProperties(Map.of(
            USERNAME, username,
            PASSWORD, password));
        sts.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY); // used for the STS client to authenticate itself to the STS provider.

        var loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        var loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);
        sts.getInInterceptors().add(loggingInInterceptor);
        sts.getOutInterceptors().add(loggingOutInterceptor);
        return sts;
    }
}
