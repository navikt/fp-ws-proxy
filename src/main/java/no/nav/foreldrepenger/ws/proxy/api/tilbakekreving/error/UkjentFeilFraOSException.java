package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

public class UkjentFeilFraOSException extends RuntimeException {

    private static final String MELDING = "FPT-539085: Fikk ukjent feil fra OS ved henting av kravgrunnlag for kravgrunnlagId=%s. %s";

    public UkjentFeilFraOSException(Long kravgrunnlagId, String infoFraKvittering) {
        super(String.format(MELDING, kravgrunnlagId, infoFraKvittering));
    }
}
