package no.nav.foreldrepenger.ws.proxy.api.simulering;

import static no.nav.foreldrepenger.ws.proxy.api.simulering.SimuleringController.SIMULERING_PATH;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringRequestMapper.tilSimulerBeregingsRequester;
import static no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.SimuleringResponsMapper.tilBeregningDtoListe;
import static no.nav.foreldrepenger.ws.proxy.config.TokenUtilConfiguration.STS_RS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.kontrakter.simulering.request.OppdragskontrollDto;
import no.nav.foreldrepenger.kontrakter.simulering.respons.BeregningDto;
import no.nav.foreldrepenger.ws.proxy.api.simulering.mapper.ØkonomioppdragMapper;
import no.nav.security.token.support.spring.ProtectedRestController;

@Validated
@ProtectedRestController(issuer = STS_RS, value = SIMULERING_PATH, claimMap = {})
public class SimuleringController {
    private static final Logger LOG = LoggerFactory.getLogger(SimuleringController.class);
    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");
    public static final String SIMULERING_PATH = "/simulering";

    private final SimuleringKlientWs simuleringKlientWs;

    public SimuleringController(SimuleringKlientWs simuleringKlientWs) {
        this.simuleringKlientWs = simuleringKlientWs;
    }

    @PostMapping("/start")
    public List<BeregningDto> simulerBeregning(@RequestBody OppdragskontrollDto oppdragskontrollDto,
                                               @RequestParam("uten_inntrekk") @DefaultValue("false") boolean utenInntrekk,
                                               @RequestParam(value = "ytelse_type", required = false) YtelseType ytelseType) {
        var tekstMedUtenInntrekk = utenInntrekk ? "uten inntrekk for" + ytelseType : "med inntrekk";
        SECURE_LOG.info("Utfører simulering {} av følgende oppdrag {}", tekstMedUtenInntrekk, oppdragskontrollDto);
        long t0 = System.currentTimeMillis();
        var behandlingId = oppdragskontrollDto.behandlingId();
        var oppdragXmlListe = new ØkonomioppdragMapper().generateOppdragXML(oppdragskontrollDto);
        var totalStørrelseUt = størrelse(oppdragXmlListe);
        var simuleringWSRequest = tilSimulerBeregingsRequester(oppdragXmlListe, ytelseType, utenInntrekk);
        LOG.info("Starter simulering {}. behandlingID={} oppdragantall={} totalstørrelseUt={}",
            tekstMedUtenInntrekk,
            behandlingId,
            oppdragXmlListe.size(),
            totalStørrelseUt);
        var simulerBeregningResponse = simuleringKlientWs.simulerBeregningene(simuleringWSRequest);
        LOG.info("Simulering {} svarmeldinger mottatt. behandlingID={} tidsforbruk={} ms",
            tekstMedUtenInntrekk,
            behandlingId,
            System.currentTimeMillis() - t0);
        var respons = tilBeregningDtoListe(simulerBeregningResponse);
        SECURE_LOG.info("Resultat av simulering {}", respons);
        return respons;
    }

    private static String størrelse(List<String> oppdragXmlListe) {
        long sum = 0;
        for (String s : oppdragXmlListe) {
            sum += s.length();
        }

        if (sum >= 1000) {
            //output i Kilobyte for enkler å lese, avrundet til hele kB
            return (sum + 500) / 1000 + " kB";
        } else {
            //output i bytes for å ha nøyaktighet når nær 0
            return sum + " B";
        }
    }



}
