package no.nav.foreldrepenger.ws.proxy.api.simulering;

import java.util.List;

import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerBeregningFeilUnderBehandling;
import no.nav.system.os.eksponering.simulerfpservicewsbinding.SimulerFpService;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningRequest;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

class SimuleringKlientWs {

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
            return klient.simulerBeregning(request);
        } catch (SimulerBeregningFeilUnderBehandling e) {
            // TODO!
        }
        return null;
    }
}
