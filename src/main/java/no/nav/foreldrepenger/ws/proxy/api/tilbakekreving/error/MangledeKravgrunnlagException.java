package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

public class MangledeKravgrunnlagException extends RuntimeException {

    private static final String MELDING = "FPT-539078: Manglende kravgrunnlag for kravgrunnlagId=%s. %s";

    public MangledeKravgrunnlagException(Long kravgrunnlagId, String infoFraKvittering) {
        super(String.format(MELDING, kravgrunnlagId, infoFraKvittering));
    }
}
