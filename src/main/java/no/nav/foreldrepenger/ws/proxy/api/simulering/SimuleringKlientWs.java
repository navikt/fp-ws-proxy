package no.nav.foreldrepenger.ws.proxy.api.simulering;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpserviceservicetypes.Oppdragslinje;

@Component
class SimuleringKlientWs {
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

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
            SECURE_LOG.info("Simulerer ");
            return klient.simulerBeregning(request);
        } catch (Exception e) {
            SECURE_LOG.info("Simulering feilet for request={}", anonymiser(request));
            return null; // TODO: Hvordan reagere på feilet simulering? Returnere null?
        }
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
