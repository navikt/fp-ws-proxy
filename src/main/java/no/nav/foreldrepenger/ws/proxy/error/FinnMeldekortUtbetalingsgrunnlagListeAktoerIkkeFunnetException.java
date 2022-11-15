package no.nav.foreldrepenger.ws.proxy.error;

import static no.nav.foreldrepenger.ws.proxy.error.ArenaForretningmessigUnntakMapper.mapForretningsmessigUnntakTilMessage;

import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet;

public class FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnetException extends RuntimeException {
    private static final String MELDING = "MeldekortUtbetalingsgrunnlag (Arena) fant ikke person for oppgitt aktørId";

    public FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnetException(FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet e) {
        super(MELDING + mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
    }
}
