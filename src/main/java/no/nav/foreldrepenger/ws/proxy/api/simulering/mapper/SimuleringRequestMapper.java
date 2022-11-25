package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.ØkonomistøtteUtils.tilSpesialkodetDatoOgKlokkeslett;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.foreldrepenger.kontrakter.simulering.request.Oppdrag110Dto;
import no.nav.foreldrepenger.kontrakter.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.Fagområde;
import no.nav.foreldrepenger.ws.proxy.api.simulering.YtelseType;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.ObjectFactory;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;

/**
 * Matcher mapper i fpoppdrag som mapper List<String> oppdragXmlListe til List<SimulerBeregningRequest>
 */
public class SimuleringRequestMapper {

    private static final String KODE_ENDRING = "ENDR";

    private SimuleringRequestMapper() {
    }


    public static List<SimulerBeregningRequest> tilSimulerBeregingsRequester(OppdragskontrollDto oppdragskontroll, YtelseType ytelseType, boolean utenInntrekk) {
        var simuleringWSRequest = SimuleringRequestMapper.tilSimulerBeregingsRequester(oppdragskontroll);
        if (utenInntrekk) {
            simuleringWSRequest = finnRequestForBrukerOgSlåAvInntrekk(simuleringWSRequest, ytelseType);
            if (simuleringWSRequest.isEmpty()) {
                throw new IllegalStateException("Utviklerfeil: Skal alltid finne requests for bruker ved simuleringsresultat med inntrekk");
            }
        }
        return simuleringWSRequest;
    }

    private static List<SimulerBeregningRequest> tilSimulerBeregingsRequester(OppdragskontrollDto oppdragskontrollDto) {
        return oppdragskontrollDto.oppdrag().stream()
            .map(oppdrag -> lagSimulerBeregningRequest(oppdrag, oppdragskontrollDto.behandlingId()))
            .toList();
    }

    private static SimulerBeregningRequest lagSimulerBeregningRequest(Oppdrag110Dto oppdrag, Long behandlingId) {
        var innerRequest = new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.ObjectFactory().createSimulerBeregningRequest();
        innerRequest.setOppdrag(OppdragMapper.mapTilSimuleringOppdrag(oppdrag, behandlingId));
        innerRequest.setSimuleringsPeriode(new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest.SimuleringsPeriode());
        return lagRequest(innerRequest);
    }

    private static SimulerBeregningRequest lagRequest(no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest simulerBeregningRequest) {
        var request = new ObjectFactory().createSimulerBeregningRequest(); // TODO: Hva er forskjellen mellom Objectmapperene her?
        request.setRequest(simulerBeregningRequest);
        return request;
    }

    private static List<SimulerBeregningRequest> finnRequestForBrukerOgSlåAvInntrekk(List<SimulerBeregningRequest> simuleringRequestListe, YtelseType ytelseType) {
        return simuleringRequestListe.stream()
            .filter(s -> Fagområde.utledFra(ytelseType).name().equals(s.getRequest().getOppdrag().getKodeFagomraade()))
            .filter(s -> s.getRequest().getOppdrag().getOppdragslinje() != null && !s.getRequest().getOppdrag().getOppdragslinje().isEmpty())
            .map(SimuleringRequestMapper::slåAvInntrekk)
            .toList();
    }

    private static SimulerBeregningRequest slåAvInntrekk(SimulerBeregningRequest request) {
        var oppdrag = request.getRequest().getOppdrag();
        var ompostering = OppdragMapper.mapOmpostring(false, oppdrag.getSaksbehId(), tilSpesialkodetDatoOgKlokkeslett(LocalDateTime.now()));
        oppdrag.setOmpostering(ompostering);
        oppdrag.setKodeEndring(KODE_ENDRING);
        return request;
    }


}
