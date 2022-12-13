package no.nav.foreldrepenger.ws.proxy.api.tilbakekreving.error;

public class UkjentFeilFraOSException extends RuntimeException {

    private static final String MELDING = "FPT-539085: Fikk ukjent feil fra OS ved henting av kravgrunnlag for behandlingId=%s og kravgrunnlagId=%s. %s";

    public UkjentFeilFraOSException(Long behandlingId, Long kravgrunnlagId, String infoFraKvittering) {
        super(String.format(MELDING, behandlingId, kravgrunnlagId, infoFraKvittering));
    }
}
