package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.SimuleringController.SIMULERING_PATH;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.SimuleringResponsDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringMapper;
import no.nav.security.token.support.spring.ProtectedRestController;
import no.nav.system.os.tjenester.simulerfpservice.simulerfpservicegrensesnitt.SimulerBeregningResponse;

@Validated
@ProtectedRestController(issuer = STS_RS, value = SIMULERING_PATH)
class SimuleringController {
    private static final Logger LOG = LoggerFactory.getLogger(SimuleringController.class);
    public static final String SIMULERING_PATH = "/simulering";

    private final SimuleringKlientWs simuleringKlientWs;

    public SimuleringController(SimuleringKlientWs simuleringKlientWs) {
        this.simuleringKlientWs = simuleringKlientWs;
    }

    @PostMapping
    // TODO: Plain text? Json? Object?
    public SimuleringResponsDto simulerBeregning(List<String> oppdragXmlListe) {
        LOG.info("Sender request til simulering hos økonomi");
        var simuleringWSRequest = SimuleringMapper.tilSimulerBeregingsRequester(oppdragXmlListe);
        var simulerBeregningResponse = simuleringKlientWs.simulerBeregningene(simuleringWSRequest);
        LOG.info("Respons fra simulering/okonomi er {}", simulerBeregningResponse);
        return tilSimuleringResponsDto(simulerBeregningResponse);
    }

    private SimuleringResponsDto tilSimuleringResponsDto(List<SimulerBeregningResponse> simulerBeregningResponse) {
        return new SimuleringResponsDto(List.of()); // TODO: Her kan vi fint endre litt på responsen. Trenger bare sende tilbaek simuleringen.
    }


}
