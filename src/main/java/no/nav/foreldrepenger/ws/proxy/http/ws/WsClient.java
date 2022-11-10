package no.nav.foreldrepenger.ws.proxy.http.ws;

import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;

import java.util.Objects;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
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

    public T createPortForSystemUser(String serviceUrl, Class<?> portType) {
        T port = createAndConfigurePort(serviceUrl, portType);
        endpointStsClientConfig.configureRequestSamlToken(port);
        return port;
    }

    public T createPortForSystemUser(String serviceUrl, String wsdl, QName service, QName portinn, Class<?> portType) {
        T port = createAndConfigurePort(serviceUrl, wsdl, service, portinn, portType);
        endpointStsClientConfig.configureRequestSamlToken(port);
        return port;
    }

    private T createAndConfigurePort(String serviceUrl, Class<?> portType) {
        return createAndConfigurePort(serviceUrl, null, null, null, portType);
    }

    @SuppressWarnings("unchecked")
    private T createAndConfigurePort(String serviceUrl, String wsdl, QName service, QName portQname, Class<?> portType) {
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(portType);
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        if (wsdl != null) {
            jaxWsProxyFactoryBean.setWsdlURL(wsdl);
        }
        if (service != null) {
            jaxWsProxyFactoryBean.setServiceName(service);
        }
        if (portQname != null) {
            jaxWsProxyFactoryBean.setEndpointName(portQname);
        }

        T port = (T) jaxWsProxyFactoryBean.create();
        Client client = ClientProxy.getClient(port);

        if (isDevOrLocal(env)) {
            var loggingInInterceptor = new LoggingInInterceptor();
            loggingInInterceptor.setPrettyLogging(true);
            var loggingOutInterceptor = new LoggingOutInterceptor();
            loggingOutInterceptor.setPrettyLogging(true);
            client.getInInterceptors().add(loggingInInterceptor);
            client.getInFaultInterceptors().add(loggingInInterceptor);
            client.getOutInterceptors().add(loggingOutInterceptor);
            client.getOutFaultInterceptors().add(loggingInInterceptor);
        }
        client.getOutInterceptors().add(new CallIdHeaderInterceptor());
        return port;
    }

}
