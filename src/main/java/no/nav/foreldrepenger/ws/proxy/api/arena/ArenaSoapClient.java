package no.nav.foreldrepenger.ws.proxy.api.arena;

import jakarta.xml.ws.soap.SOAPFaultException;
import no.nav.foreldrepenger.ws.proxy.error.FinnesIkkeException;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.foreldrepenger.ws.proxy.error.SikkerhetsbegrensingException;
import no.nav.foreldrepenger.ws.proxy.error.UgyldigInputException;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.feil.ForretningsmessigUnntak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeResponse;
import org.springframework.stereotype.Component;


/**
 * Integrasjon mot Arena - tjeneste MeldekortUtbetalingsgrunnlag
 *
 * @see https://confluence.adeo.no/display/SDFS/tjeneste_v3%3Avirksomhet%3AmeldekortUtbetalingsgrunnlag_v1
 */
@Component
public class ArenaSoapClient {
    private final MeldekortUtbetalingsgrunnlagV1 klient;

    public ArenaSoapClient(MeldekortUtbetalingsgrunnlagV1 klient) {
        this.klient = klient;
    }

    public FinnMeldekortUtbetalingsgrunnlagListeResponse finnMeldekortUtbetalingsgrunnlagListe(FinnMeldekortUtbetalingsgrunnlagListeRequest arenaWSRequest) {
        try {
            return klient.finnMeldekortUtbetalingsgrunnlagListe(arenaWSRequest);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning e) {
            throw new SikkerhetsbegrensingException("MeldekortUtbetalingsgrunnlag (Arena) ikke tilgjengelig (sikkerhetsbegrensning)", mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput e) {
            throw new UgyldigInputException("MeldekortUtbetalingsgrunnlag (Arena) ugyldig input", mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet e) {
            throw new FinnesIkkeException("MeldekortUtbetalingsgrunnlag (Arena) fant ikke person for oppgitt aktørId", mapForretningsmessigUnntakTilMessage(e.getFaultInfo()), e);
        } catch (SOAPFaultException e) {
            throw new GenerellSoapFaultException("SOAP tjenesten [ MeldekortUtbetalingsgrunnlagV1 ] returnerte en SOAP Fault", e);
        }
    }

    private static String mapForretningsmessigUnntakTilMessage(ForretningsmessigUnntak forretningsmessigUnntak) {
        if (forretningsmessigUnntak == null) {
            return " med ingen Faultinfo.";
        }
        return String.format(" med feilkilde: %s, feilaarsak: %s, feilmelding: %s",
            forretningsmessigUnntak.getFeilkilde(),
            forretningsmessigUnntak.getFeilaarsak(),
            forretningsmessigUnntak.getFeilmelding());
    }
}
