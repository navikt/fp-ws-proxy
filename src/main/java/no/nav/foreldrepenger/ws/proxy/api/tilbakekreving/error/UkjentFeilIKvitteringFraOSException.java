package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

public class UkjentFeilIKvitteringFraOSException extends RuntimeException {

    public UkjentFeilIKvitteringFraOSException (String feilmelding) {
        super(feilmelding);
    }
}
