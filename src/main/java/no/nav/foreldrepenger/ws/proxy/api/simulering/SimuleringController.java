package no.nav.foreldrepenger.ws.proxy.api.simulering;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.fpwsproxy.simulering.respons.BeregningDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.YtelseType;
import no.nav.foreldrepenger.ws.proxy.http.ProtectedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringRequestMapper.tilSimulerBeregingsRequester;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringResponsMapper.tilBeregningDtoListe;

@ProtectedRestController(value = "/simulering")
public class SimuleringController {
    private static final Logger LOG = LoggerFactory.getLogger(SimuleringController.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");

    private final SimuleringSoapClient simuleringSoapClient;

    public SimuleringController(SimuleringSoapClient simuleringSoapClient) {
        this.simuleringSoapClient = simuleringSoapClient;
    }

    @PostMapping("/start")
    public List<BeregningDto> simulerBeregning(@Valid @NotNull @RequestBody OppdragskontrollDto oppdragskontrollDto,
                                               @RequestParam("uten_inntrekk") @DefaultValue("false") boolean utenInntrekk,
                                               @RequestParam(value = "ytelse_type", required = false) YtelseType ytelseType) {
        var tekstMedUtenInntrekk = utenInntrekk ? "uten inntrekk" + ytelseType : "med inntrekk";
        try {
            long t0 = System.currentTimeMillis();
            var behandlingId = oppdragskontrollDto.behandlingId();
            var simuleringWSRequest = tilSimulerBeregingsRequester(oppdragskontrollDto, ytelseType, utenInntrekk);
            LOG.info("Starter simulering {} for behandlingID={} med oppdragantall={}",
                tekstMedUtenInntrekk,
                behandlingId,
                simuleringWSRequest.size());

            var simulerBeregningResponse = simuleringSoapClient.simulerBeregningene(simuleringWSRequest);
            LOG.info("Simulering {} svarmeldinger mottatt for behandlingID={} tidsforbruk={} ms",
                tekstMedUtenInntrekk,
                behandlingId,
                System.currentTimeMillis() - t0);
            return tilBeregningDtoListe(simulerBeregningResponse);
        } catch (Exception e) {
            SECURE_LOG.info("Simulering {} av følgende oppdrag feilet: {}", tekstMedUtenInntrekk, oppdragskontrollDto);
            throw e;
        }
    }

}
