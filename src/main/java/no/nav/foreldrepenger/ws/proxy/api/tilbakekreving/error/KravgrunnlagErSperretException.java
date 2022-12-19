package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

public class KravgrunnlagErSperretException extends RuntimeException {

    private static final String MELDING = "FPT-539078: Kravgrunnlag sperret for kravgrunnlagId=%s. %s";

    public KravgrunnlagErSperretException(Long kravgrunnlagId, String infoFraKvittering) {
        super(String.format(MELDING, kravgrunnlagId, infoFraKvittering));
    }
}
