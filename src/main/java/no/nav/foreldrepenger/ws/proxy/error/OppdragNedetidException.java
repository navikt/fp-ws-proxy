package no.nav.foreldrepenger.ws.proxy.error;

public class OppdragNedetidException extends RuntimeException {

    private static final String MELDING = "Kallet mot oppdragsystemet feilet. Feilmelding og tidspunktet tilsier at oppdragsystemet har forventet nedetid (utenfor åpningstid).";

    public OppdragNedetidException(Throwable cause) {
        super(MELDING, cause);
    }

}
