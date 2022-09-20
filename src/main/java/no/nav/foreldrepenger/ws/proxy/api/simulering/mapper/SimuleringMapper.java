package no.nav.foreldrepenger.ws.proxy.api.simulering.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.SimuleringRequestDto;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.ObjectFactory;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdrag;

public class SimuleringMapper {

    public static List<SimulerBeregningRequest> tilSimulerBeregingsRequester(List<String> oppdragXmlListe) {
        return oppdragXmlListe.stream()
            .map(OppdragMapper::unmarshalOppdragOgKonverter)
            .map(SimuleringMapper::lagSimulerBeregningRequest)
            .toList();
    }

    private static SimulerBeregningRequest lagSimulerBeregningRequest(Oppdrag oppdrag) {
        var innerRequest = new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.ObjectFactory().createSimulerBeregningRequest();
        innerRequest.setOppdrag(oppdrag);
        innerRequest.setSimuleringsPeriode(new no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest.SimuleringsPeriode());
        return lagRequest(innerRequest);
    }

    private static SimulerBeregningRequest lagRequest(no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.SimulerBeregningRequest simulerBeregningRequest) {
        SimulerBeregningRequest request = new ObjectFactory().createSimulerBeregningRequest();
        request.setRequest(simulerBeregningRequest);
        return request;
    }
}
