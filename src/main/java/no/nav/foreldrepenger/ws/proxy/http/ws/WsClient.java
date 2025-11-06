package no.nav.foreldrepenger.ws.proxy.http.ws;

import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static no.nav.foreldrepenger.ws.proxy.util.EnvUtil.isDevOrLocal;
import static org.apache.cxf.rt.security.SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT;
import static org.apache.cxf.rt.security.SecurityConstants.STS_CLIENT;

@Component
public abstract class WsClient<T> {
    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_REQUEST_SAML_POLICY = POLICY_PATH + "requestSamlPolicy.xml";

    private final STSClient stsClient;
    private final Environment env;

    protected WsClient(STSClient stsClient, Environment env) {
        this.stsClient = stsClient;
        this.env = env;
    }

    public T configureClientForSystemUser(T port) {
        configureClientWithLoggingAndCallId(port);
        configureRequestSamlToken(port);
        return port;
    }

    private void configureRequestSamlToken(T port) {
        var client = ClientProxy.getClient(port);
        client.getRequestContext().put(STS_CLIENT, stsClient);
        client.getRequestContext().put(CACHE_ISSUED_TOKEN_IN_ENDPOINT, true);
        setEndpointPolicyReference(client, STS_REQUEST_SAML_POLICY);
    }

    private static void setEndpointPolicyReference(Client client, String uri) {
        var policy = resolvePolicyReference(client, uri);
        setClientEndpointPolicy(client, policy);
    }

    private static Policy resolvePolicyReference(Client client, String uri) {
        var policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        return new RemoteReferenceResolver("", policyBuilder).resolveReference(uri);
    }

    private static void setClientEndpointPolicy(Client client, Policy policy) {
        var endpoint = client.getEndpoint();
        var endpointInfo = endpoint.getEndpointInfo();

        var policyEngine = client.getBus().getExtension(PolicyEngine.class);
        var message = new SoapMessage(Soap12.getInstance());
        var endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }

    private void configureClientWithLoggingAndCallId(T port) {
        var client = ClientProxy.getClient(port);
        client.getOutInterceptors().add(new CallIdHeaderInterceptor());

        if (isDevOrLocal(env)) {
            var loggingInInterceptor = new LoggingInInterceptor();
            loggingInInterceptor.setPrettyLogging(true);
            var loggingOutInterceptor = new LoggingOutInterceptor();
            loggingOutInterceptor.setPrettyLogging(true);
            client.getInInterceptors().add(loggingInInterceptor);
            client.getInFaultInterceptors().add(loggingInInterceptor);
            client.getOutInterceptors().add(loggingOutInterceptor);
            client.getOutFaultInterceptors().add(loggingOutInterceptor);
        }
    }
}
