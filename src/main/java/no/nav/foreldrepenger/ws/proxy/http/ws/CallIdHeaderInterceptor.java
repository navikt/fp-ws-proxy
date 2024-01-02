package no.nav.foreldrepenger.ws.proxy.http.ws;

import jakarta.xml.bind.JAXBException;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;

public class CallIdHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = LoggerFactory.getLogger(CallIdHeaderInterceptor.class);
    private static final String NAV_CALL_ID = "Nav-CallId";

    public CallIdHeaderInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            var qName = new QName("uri:no.nav.applikasjonsrammeverk", "callId");
            var header = new SoapHeader(qName, MDC.get(NAV_CALL_ID), new JAXBDataBinding(String.class));
            ((SoapMessage) message).getHeaders().add(header);
        } catch (JAXBException ex) {
            logger.warn("Error while setting CallId header", ex);
        }
    }
}
