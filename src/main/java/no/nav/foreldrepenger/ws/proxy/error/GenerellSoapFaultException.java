package no.nav.foreldrepenger.ws.proxy.error;

import javax.xml.ws.WebServiceException;

public class GenerellSoapFaultException extends RuntimeException {

    public GenerellSoapFaultException(String melding, WebServiceException e) {
        super(melding, e);
    }
}
