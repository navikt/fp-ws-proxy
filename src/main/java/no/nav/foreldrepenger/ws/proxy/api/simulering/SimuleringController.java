package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.SimuleringController.SIMULERING_PATH;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringRequestMapper.tilSimulerBeregingsRequester;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringResponsMapper.tilBeregningDtoListe;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.BeregningDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.dto.YtelseType;
import no.nav.security.token.support.spring.ProtectedRestController;

@Validated
@ProtectedRestController(issuer = STS_RS, value = SIMULERING_PATH)
class SimuleringController {
    public static final String SIMULERING_PATH = "/simulering";

    private final SimuleringKlientWs simuleringKlientWs;

    public SimuleringController(SimuleringKlientWs simuleringKlientWs) {
        this.simuleringKlientWs = simuleringKlientWs;
    }

    @PostMapping
    public List<BeregningDto> simulerBeregning(@RequestBody List<String> oppdragXmlListe,
                                               @RequestParam("ytelseType") YtelseType ytelseType,
                                               @RequestParam("utenInntrekk") boolean utenInntrekk) {
        var simuleringWSRequest = tilSimulerBeregingsRequester(oppdragXmlListe, ytelseType, utenInntrekk);
        var simulerBeregningResponse = simuleringKlientWs.simulerBeregningene(simuleringWSRequest);
        return tilBeregningDtoListe(simulerBeregningResponse);
    }



}
