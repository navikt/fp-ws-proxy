package no.nav.foreldrepenger.ws.proxy.api.simulering;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.ws.proxy.api.simulering.util.XmlStringFieldFikser;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;
import no.nav.system.os.tjenester.simulerfpservice.feil.FeilUnderBehandling;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje;
import no.nav.vedtak.exception.IntegrasjonException;

@Component
class SimuleringKlientWs {
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    /**
     * oppdragssytemet (OS) har offisiell åpningstid mandag-fredag 0700-1900, men det er ofte åpent utenom de offisielle åpningstidene.
     * utenom åpningstid styrer vi logging slik at feilmeldinger fra OS som vanligvis forekommer ved nedetid logges som Info, for å ikke skape støy i loggene
     */
    private static final Set<DayOfWeek> OPPDRAG_ÅPNE_DAGER = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    private static final LocalTime OPPDRAG_ÅPNINGSTID_START = LocalTime.of(7, 0);
    private static final LocalTime OPPDRAG_ÅPNINGSTID_SLUTT = LocalTime.of(19, 0);
    static final List<String> TYPISKE_FEILMELDING_NÅR_OPPDRAG_ER_NEDE = List.of(
        "Unexpected EOF in prolog",
        "Could not send Message",
        "Error writing request body to server"
    );


    private final SimulerFpService klient;

    public SimuleringKlientWs(SimulerFpService klient) {
        this.klient = klient;
    }

    public List<SimulerBeregningResponse> simulerBeregningene(List<SimulerBeregningRequest> request) {
        return request.stream()
            .map(this::simulerBeregning)
            .toList();
    }

    public SimulerBeregningResponse simulerBeregning(SimulerBeregningRequest request) {
        try {
            var respons = klient.simulerBeregning(request);
            XmlStringFieldFikser.stripTrailingSpacesFromStrings(respons);
            return klient.simulerBeregning(request);
        } catch (SimulerBeregningFeilUnderBehandling e) {
            SECURE_LOG.info("Simulering feilet for request={}", anonymiser(request), e);
            FeilUnderBehandling fault = e.getFaultInfo();
            throw new IntegrasjonException("FPO-845125", String.format("Simulering feilet. Mottok feilmelding fra oppdragsystemet: source='%s' type='%s' message='%s' rootcause='%s' timestamp='%s'", fault.getErrorSource(), fault.getErrorType(), fault.getErrorMessage(), fault.getRootCause(), fault.getDateTimeStamp()), e);
        } catch (WebServiceException e) {
            SECURE_LOG.info("Simulering feilet for request={}", anonymiser(request), e);
            if (feiletPgaOppdragsystemetUtenforÅpningstid(e)) {
                throw new OppdragNedetidException("FPO-273196", "Kallet mot oppdragsystemet feilet. Feilmelding og tidspunktet tilsier at oppdragsystemet har forventet nedetid (utenfor åpningstid).", e);
            }
            throw new IntegrasjonException("FPO-852145", "Simulering feilet. Fikk uventet feil mot oppdragssytemet", e);
        }
    }

    private boolean feiletPgaOppdragsystemetUtenforÅpningstid(WebServiceException e) {
        return !innenforÅpningstid() && TYPISKE_FEILMELDING_NÅR_OPPDRAG_ER_NEDE.stream().anyMatch(typisk -> e.getMessage().contains(typisk));
    }

    boolean innenforÅpningstid() {
        LocalDateTime nå = LocalDateTime.now();
        return OPPDRAG_ÅPNE_DAGER.contains(nå.getDayOfWeek())
            && nå.toLocalTime().isAfter(OPPDRAG_ÅPNINGSTID_START)
            && nå.toLocalTime().isBefore(OPPDRAG_ÅPNINGSTID_SLUTT);
    }

    private SimulerBeregningRequest anonymiser(SimulerBeregningRequest request) {
        request.getRequest().getOppdrag().setOppdragGjelderId(" *** ");
        request.getRequest().getOppdrag().getOppdragslinje().forEach(this::anonymiser);
        return request;
    }

    private void anonymiser(Oppdragslinje ol) {
        if (ol.getRefusjonsInfo() != null) {
            ol.getRefusjonsInfo().setRefunderesId(" *** ");}
    }
}
