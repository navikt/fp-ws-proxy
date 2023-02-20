package no.nav.foreldrepenger.ws.proxy.http.ws;

import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class WsClient<T> {

    private final EndpointSTSClientConfig endpointStsClientConfig;
    private final Environment env;

    public WsClient(EndpointSTSClientConfig endpointStsClientConfig, Environment env) {
        this.endpointStsClientConfig = endpointStsClientConfig;
        this.env = env;
    }

    public T configureClientForSystemUser(T port) {
        configureClientWithLoggingAndCallId(port);
        endpointStsClientConfig.configureRequestSamlToken(port);
        return port;
    }

    private T configureClientWithLoggingAndCallId(T port) {
        Client client = ClientProxy.getClient(port);
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
        return port;
    }
}
