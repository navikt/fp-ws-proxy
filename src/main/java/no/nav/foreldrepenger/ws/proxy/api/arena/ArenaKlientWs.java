package no.nav.foreldrepenger.ws.proxy.api.arena;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.foreldrepenger.ws.proxy.http.PingEndpointAware;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.binding.MeldekortUtbetalingsgrunnlagV1;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.feil.ForretningsmessigUnntak;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeRequest;
import no.nav.tjeneste.virksomhet.meldekortutbetalingsgrunnlag.v1.meldinger.FinnMeldekortUtbetalingsgrunnlagListeResponse;


/**
 * Integrasjon mot Arena - tjeneste MeldekortUtbetalingsgrunnlag
 *
 * @see https://confluence.adeo.no/display/SDFS/tjeneste_v3%3Avirksomhet%3AmeldekortUtbetalingsgrunnlag_v1
 */
@Component
public class ArenaKlientWs implements PingEndpointAware {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaKlientWs.class);

    private final MeldekortUtbetalingsgrunnlagV1 klient;

    public ArenaKlientWs(MeldekortUtbetalingsgrunnlagV1 klient) {
        this.klient = klient;
    }

    public FinnMeldekortUtbetalingsgrunnlagListeResponse finnMeldekortUtbetalingsgrunnlagListe(FinnMeldekortUtbetalingsgrunnlagListeRequest arenaWSRequest) {
        try {
            return klient.finnMeldekortUtbetalingsgrunnlagListe(arenaWSRequest);
        } catch (SOAPFaultException ex) {
            throw ex; // TODO: Bare returnere en 500 feil?
        } catch (FinnMeldekortUtbetalingsgrunnlagListeAktoerIkkeFunnet ex) {
            throw create(HttpStatus.FORBIDDEN, mapForretningsmessigUnntakTilMessage(ex.getFaultInfo()));
        } catch (FinnMeldekortUtbetalingsgrunnlagListeSikkerhetsbegrensning ex) {
            throw create(HttpStatus.BAD_REQUEST, mapForretningsmessigUnntakTilMessage(ex.getFaultInfo()));
        } catch (FinnMeldekortUtbetalingsgrunnlagListeUgyldigInput ex) {
            throw create(HttpStatus.NOT_FOUND, mapForretningsmessigUnntakTilMessage(ex.getFaultInfo()));
        }
    }

    private static String mapForretningsmessigUnntakTilMessage(ForretningsmessigUnntak forretningsmessigUnntak) {
        if (forretningsmessigUnntak == null) {
            return null;
        }

        var formatFeilmelding = String.format("Feilkilde: %s, feilaarsak: %s, feilmelding: %s",
            forretningsmessigUnntak.getFeilkilde(),
            forretningsmessigUnntak.getFeilaarsak(),
            forretningsmessigUnntak.getFeilmelding());
        LOG.info("Noe gikk galt i kall mot arena {}", formatFeilmelding);
        return formatFeilmelding;
    }

    private static HttpClientErrorException create(HttpStatus status, String statustekst) {
        return HttpClientErrorException.create(status, statustekst, null, null, null);
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
