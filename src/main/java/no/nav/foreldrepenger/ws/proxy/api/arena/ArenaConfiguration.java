package no.nav.foreldrepenger.ws.proxy.api.arena;

import no.nav.foreldrepenger.ws.proxy.http.ws.WsClient;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.xml.namespace.QName;
import java.util.Objects;

@Configuration
public class ArenaConfiguration extends WsClient<MeldekortUtbetalingsgrunnlagV1> {

    private static final String WSDL = "wsdl/no/nav/tjeneste/virksomhet/meldekortUtbetalingsgrunnlag/v1/Binding.wsdl";
    private static final String NAMESPACE = "http://nav.no/tjeneste/virksomhet/meldekortUtbetalingsgrunnlag/v1/Binding";
    private static final QName SERVICE = new QName(NAMESPACE, "MeldekortUtbetalingsgrunnlag_v1");
    private static final QName PORT = new QName(NAMESPACE, "meldekortUtbetalingsgrunnlag_v1Port");

    public ArenaConfiguration(STSClient stsClient, Environment env) {
        super(stsClient, env);
    }

    @Bean
    public MeldekortUtbetalingsgrunnlagV1 klient(@Value("${meldekortutbetalingsgrunnlag.v1.url}") String serviceUrl) {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        jaxWsProxyFactoryBean.setServiceClass(MeldekortUtbetalingsgrunnlagV1.class);
        jaxWsProxyFactoryBean.setWsdlURL(WSDL);
        jaxWsProxyFactoryBean.setServiceName(SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(PORT);
        var port = (MeldekortUtbetalingsgrunnlagV1) jaxWsProxyFactoryBean.create();
        return configureClientForSystemUser(port);
    }
}
