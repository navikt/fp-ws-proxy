package no.nav.foreldrepenger.ws.proxy.api.arena;

import org.springframework.stereotype.Component;

import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeResponse;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.TekniskException;

@Component
class ArenaKlientWs {

    private final MeldekortUtbetalingsgrunnlagV1 klient;

    public ArenaKlientWs(MeldekortUtbetalingsgrunnlagV1 klient) {
        this.klient = klient;
    }

    public FinnMeldekortUtbetalingsgrunnlagListeResponse finnMeldekortUtbetalingsgrunnlagListe(FinnMeldekortUtbetalingsgrunnlagListeRequest arenaWSRequest) {
        try {
            return klient.finnMeldekortUtbetalingsgrunnlagListe(arenaWSRequest);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning e) {
            throw new TekniskException("FP-150919", "MeldekortUtbetalingsgrunnlag (Arena) ikke tilgjengelig (sikkerhetsbegrensning)", e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput e) {
            throw new IntegrasjonException("FP-615299", "MeldekortUtbetalingsgrunnlag (Arena) ugyldig input", e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet e) {
            throw new IntegrasjonException("FP-615298", "MeldekortUtbetalingsgrunnlag (Arena) fant ikke person for oppgitt aktørId", e);
        }
    }
}
