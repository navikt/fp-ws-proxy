package no.nav.foreldrepenger.ws.proxy.error;

import static no.nav.foreldrepenger.ws.proxy.error.ArenaForretningmessigUnntakMapper.mapForretningsmessigUnntakTilMessage;

import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput;

public class FinnMeldekortUtbetalingsgrunnlagListeUgyldigInputException extends RuntimeException {
    private static final String MELDING = "MeldekortUtbetalingsgrunnlag (Arena) ugyldig input";

    public FinnMeldekortUtbetalingsgrunnlagListeUgyldigInputException(FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput e) {
        super(MELDING + mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
    }
}
