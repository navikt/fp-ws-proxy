package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

import no.nav.vedtak.exception.IntegrasjonException;

public class ManglendeKravgrunnlagException extends IntegrasjonException {
    public ManglendeKravgrunnlagException(String kode, String msg) {
        this(kode, msg, (Throwable) null);
    }

    public ManglendeKravgrunnlagException(String kode, String msg, Throwable cause) {
        super(kode, msg, cause);
    }
}
