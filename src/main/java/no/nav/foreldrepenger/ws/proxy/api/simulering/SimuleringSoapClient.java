package no.nav.foreldrepenger.ws.proxy.api.simulering;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.api.simulering.error.OppdragNedetidException;
import no.nav.foreldrepenger.ws.proxy.api.simulering.util.XmlStringFieldFikser;
import no.nav.foreldrepenger.ws.proxy.error.GenerellSoapFaultException;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;
import no.nav.system.os.tjenester.simulerfpservice.feil.FeilUnderBehandling;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

@Component
class SimuleringSoapClient {
    /**
     * oppdragssytemet (OS) har offisiell åpningstid mandag-fredag 0700-1900, men det er ofte åpent utenom de offisielle åpningstidene.
     * utenom åpningstid styrer vi logging slik at feilmeldinger fra OS som vanligvis forekommer ved nedetid logges som Info, for å ikke skape støy i loggene
     */
    private static final Set<DayOfWeek> OPPDRAG_ÅPNE_DAGER = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    private static final LocalTime OPPDRAG_ÅPNINGSTID_START = LocalTime.of(7, 0);
    private static final LocalTime OPPDRAG_ÅPNINGSTID_SLUTT = LocalTime.of(19, 0);
    private static final List<String> TYPISKE_FEILMELDING_NÅR_OPPDRAG_ER_NEDE = List.of(
        "Unexpected EOF in prolog",
        "Could not send Message",
        "Error writing request body to server"
    );


    private final SimulerFpService klient;

    public SimuleringSoapClient(SimulerFpService klient) {
        this.klient = klient;
    }

    public List<SimulerBeregningResponse> simulerBeregningene(List<SimulerBeregningRequest> request) {
        return request.stream()
            .map(this::simulerBeregning)
            .toList();
    }

    /**
     * Simulerer oppdrag mot oppdragssystemet (OS). Krever gyldig SAML token.
     * Og for authorisering så må service brukeren være lagt inn i whitelist
     * @param request
     * @return
     */
    public SimulerBeregningResponse simulerBeregning(SimulerBeregningRequest request) {
        try {
            var respons = klient.simulerBeregning(request);
            XmlStringFieldFikser.stripTrailingSpacesFromStrings(respons);
            return respons;
        } catch (SimulerBeregningFeilUnderBehandling e) {
            throw new GenerellSoapFaultException(feilUnderBehandlingTilMessage(e.getFaultInfo()), e);
        } catch (WebServiceException e) {
            if (feiletPgaOppdragsystemetUtenforÅpningstid(e)) {
                throw new OppdragNedetidException(e);
            }
            throw new GenerellSoapFaultException("Simulering feilet. Fikk uventet feil mot oppdragssytemet", e);
        }
    }
    private static String feilUnderBehandlingTilMessage(FeilUnderBehandling fault) {
        if (fault == null) {
            return "Simulering feilet. Feil under behandling.";
        }
        return String.format("Simulering feilet. Mottok feilmelding fra oppdragsystemet: source='%s' type='%s' message='%s' rootcause='%s' timestamp='%s'",
            fault.getErrorSource(), fault.getErrorType(), fault.getErrorMessage(), fault.getRootCause(), fault.getDateTimeStamp());
    }

    private static boolean feiletPgaOppdragsystemetUtenforÅpningstid(WebServiceException e) {
        return !innenforÅpningstid() && TYPISKE_FEILMELDING_NÅR_OPPDRAG_ER_NEDE.stream().anyMatch(typisk -> e.getMessage().contains(typisk));
    }

    private static boolean innenforÅpningstid() {
        LocalDateTime nå = LocalDateTime.now();
        return OPPDRAG_ÅPNE_DAGER.contains(nå.getDayOfWeek())
            && nå.toLocalTime().isAfter(OPPDRAG_ÅPNINGSTID_START)
            && nå.toLocalTime().isBefore(OPPDRAG_ÅPNINGSTID_SLUTT);
    }

}
