package no.nav.foreldrepenger.ws.proxy.error;

import static no.nav.foreldrepenger.ws.proxy.error.ArenaForretningmessigUnntakMapper.mapForretningsmessigUnntakTilMessage;

import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning;

public class FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensingException extends RuntimeException {
    private static final String MELDING = "MeldekortUtbetalingsgrunnlag (Arena) ikke tilgjengelig (sikkerhetsbegrensning)";

    public FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensingException(FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning e) {
        super(MELDING + mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
    }
}
