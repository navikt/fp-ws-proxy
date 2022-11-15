package no.nav.foreldrepenger.ws.proxy.error;

import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.feil.ForretningsmessigUnntak;

class ArenaForretningmessigUnntakMapper {

    private ArenaForretningmessigUnntakMapper() {
    }

    static String mapForretningsmessigUnntakTilMessage(ForretningsmessigUnntak forretningsmessigUnntak) {
        if (forretningsmessigUnntak == null) {
            return " med ingen Faultinfo.";
        }
        return String.format(" med feilkilde: %s, feilaarsak: %s, feilmelding: %s",
            forretningsmessigUnntak.getFeilkilde(),
            forretningsmessigUnntak.getFeilaarsak(),
            forretningsmessigUnntak.getFeilmelding());
    }

}
