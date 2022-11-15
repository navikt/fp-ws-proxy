package no.nav.foreldrepenger.ws.proxy.api.arena;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.error.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnetException;
import no.nav.foreldrepenger.ws.proxy.error.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensingException;
import no.nav.foreldrepenger.ws.proxy.error.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInputException;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.foreldrepenger.ws.proxy.http.PingEndpointAware;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeResponse;


/**
 * Integrasjon mot Arena - tjeneste MeldekortUtbetalingsgrunnlag
 *
 * @see https://confluence.adeo.no/display/SDFS/tjeneste_v3%3Avirksomhet%3AmeldekortUtbetalingsgrunnlag_v1
 */
@Component
public class ArenaKlientWs implements PingEndpointAware {
    private final MeldekortUtbetalingsgrunnlagV1 klient;

    public ArenaKlientWs(MeldekortUtbetalingsgrunnlagV1 klient) {
        this.klient = klient;
    }

    public FinnMeldekortUtbetalingsgrunnlagListeResponse finnMeldekortUtbetalingsgrunnlagListe(FinnMeldekortUtbetalingsgrunnlagListeRequest arenaWSRequest) {
        try {
            return klient.finnMeldekortUtbetalingsgrunnlagListe(arenaWSRequest);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning e) {
            throw new FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensingException(e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput e) {
            throw new FinnMeldekortUtbetalingsgrunnlagListeUgyldigInputException(e);
        } catch (FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet e) {
            throw new FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnetException(e);
        } catch (SOAPFaultException e) {
            throw new GenerellSoapFaultException("SOAP tjenesten [ MeldekortUtbetalingsgrunnlagV1 ] returnerte en SOAP Fault", e);
        }
    }

    @Override
    public String name() {
        return "Arena [MeldekortUtbetalingsgrunnlagV1]";
    }

    @Override
    public void ping() {
        klient.ping();
    }
}
