package no.nav.foreldrepenger.ws.proxy.api.simulering;

import no.nav.vedtak.exception.IntegrasjonException;

public class OppdragNedetidException extends IntegrasjonException {
    public OppdragNedetidException(String kode, String melding, Throwable cause) {
        super(kode, melding, cause);
    }

}
