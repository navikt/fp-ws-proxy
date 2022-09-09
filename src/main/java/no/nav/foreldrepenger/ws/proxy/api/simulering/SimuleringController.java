package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.SimuleringController.SIMULERING_PATH;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;

import no.nav.security.token.support.spring.ProtectedRestController;

@ProtectedRestController(issuer = STS, value = SIMULERING_PATH)
class SimuleringController {
    private static final Logger LOG = LoggerFactory.getLogger(SimuleringController.class);
    public static final String SIMULERING_PATH = "/simulering";

    private final SimuleringTjeneste simuleringTjeneste;

    public SimuleringController(SimuleringTjeneste simuleringTjeneste) {
        this.simuleringTjeneste = simuleringTjeneste;
    }

    @PostMapping
    public void sendTilArena(SimuleringDto simuleringDto) {
        LOG.info("Sender request til simulering hos økonomi");
        var simuleringWSRequest = SimuleringMapperWS.tilWS(simuleringDto);
        simuleringTjeneste.sendWSRequest(simuleringWSRequest);
    }
}
