package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.ws.proxy.api.simulering.Fagområde;
import no.nav.foreldrepenger.ws.proxy.api.simulering.YtelseType;
import no.nav.system.os.entiteter.oppdragskjema.Ompostering;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.ObjectFactory;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag;

/**
 * Matcher mapper i fpoppdrag som mapper List<String> oppdragXmlListe til List<SimulerBeregningRequest>
 */
public class SimuleringRequestMapper {

    private static final String KODE_ENDRING = "ENDR";

    private SimuleringRequestMapper() {
    }


    public static List<SimulerBeregningRequest> tilSimulerBeregingsRequester(List<String> oppdragXmlListe, YtelseType ytelseType, boolean utenInntrekk) {
        var simuleringWSRequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragXmlListe);
        if (utenInntrekk) {
            simuleringWSRequest = finnRequestForBrukerOgSlåAvInntrekk(simuleringWSRequest, ytelseType);
            if (simuleringWSRequest.isEmpty()) {
                throw new IllegalStateException("Utviklerfeil: Skal alltid finne requests for bruker ved simuleringsresultat med inntrekk");
            }
        }
        return simuleringWSRequest;
    }

    private static List<SimulerBeregningRequest> tilSimulerBeregingsRequester(List<String> oppdragXmlListe) {
        return oppdragXmlListe.stream()
            .map(OppdragMapper::unmarshalOppdragOgKonverter)
            .map(SimuleringRequestMapper::lagSimulerBeregningRequest)
            .toList();
    }

    private static SimulerBeregningRequest lagSimulerBeregningRequest(Oppdrag oppdrag) {
        var innerRequest = new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.ObjectFactory().createSimulerBeregningRequest();
        innerRequest.setOppdrag(oppdrag);
        innerRequest.setSimuleringsPeriode(new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest.SimuleringsPeriode());
        return lagRequest(innerRequest);
    }

    private static SimulerBeregningRequest lagRequest(no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest simulerBeregningRequest) {
        SimulerBeregningRequest request = new ObjectFactory().createSimulerBeregningRequest(); // TODO: Hva er forskjellen mellom Objectmapperene her?
        request.setRequest(simulerBeregningRequest);
        return request;
    }

    private static List<SimulerBeregningRequest> finnRequestForBrukerOgSlåAvInntrekk(List<SimulerBeregningRequest> simuleringRequestListe, YtelseType ytelseType) {
        return simuleringRequestListe.stream()
            .filter(s -> Fagområde.utledFra(ytelseType).name().equals(s.getRequest().getOppdrag().getKodeFagomraade()))
            .filter(s -> s.getRequest().getOppdrag().getOppdragslinje() != null && !s.getRequest().getOppdrag().getOppdragslinje().isEmpty())
            .map(SimuleringRequestMapper::slåAvInntrekk)
            .collect(Collectors.toList());
    }

    private static SimulerBeregningRequest slåAvInntrekk(SimulerBeregningRequest request) {
        Oppdrag oppdrag = request.getRequest().getOppdrag();
        Ompostering ompostering = OppdragMapper.mapOmpostering(oppdrag.getSaksbehId(), "N");
        oppdrag.setOmpostering(ompostering);
        oppdrag.setKodeEndring(KODE_ENDRING);
        return request;
    }


}
