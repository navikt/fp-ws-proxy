package no.nav.foreldrepenger.ws.proxy.error;

public class GenerellSoapFaultException extends RuntimeException {

    public GenerellSoapFaultException(String melding, Exception e) {
        super(melding, e);
    }
}
